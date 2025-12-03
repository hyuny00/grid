import asyncio
import csv
import json
import os
from datetime import datetime, date
from pathlib import Path
from typing import Dict, Optional
from realtime_stock_client import (
    RealtimeStockClient,
    load_access_token,
    load_best_stock_info,
)
from stock_trading import StockTrader
from realtime_position_client import RealtimePositionClient
from deposit_manager import KiwoomDepositInquiry

"""

todo: 매도된금액으로 다시 수량계산. 자금관리 
      
      

분할매도 추가

추천주식 수정시 어떻게 반영할것인지.
스케줄작업필요함


익절가: 전일종가 기준 (예: 전일종가 100,000원 → +4% = 104,000원)
손절가: 매수가 기준으로 재계산됨
v4 : 모든 잔고 status.json에 저장 (수동보유 종목 포함) + 수동보유 종목 자동매매 추가 기능

수동 보유 → 자동매도 기능
auto_trader.add_manual_stock_to_bot("005930", sell_price=75000, stop_loss_rate=-3.0)

전체 포트폴리오 조회
auto_trader.get_all_positions()  # 추적 종목 + 기타 종목 모두 표시
"""


def load_previous_close(stock_code, data_folder="price_data"):
    """CSV 파일에서 전일 종가 조회"""
    csv_path = os.path.join(data_folder, f"{stock_code}_daily_20days.csv")

    if not os.path.exists(csv_path):
        print(f"⚠️  경고: CSV 파일을 찾을 수 없습니다: {csv_path}")
        return None

    try:
        with open(csv_path, "r", encoding="utf-8-sig") as f:
            reader = csv.DictReader(f)
            rows = list(reader)
            if len(rows) < 1:
                print(f"⚠️  경고: 데이터가 없습니다: {csv_path}")
                return None

            previous_day = rows[0]
            previous_close = int(previous_day["close"])
            print(
                f"📅 [{stock_code}] 전일 종가 로드: {previous_close:,}원 (날짜: {previous_day['date']})"
            )
            return previous_close

    except Exception as e:
        print(f"❌ CSV 파일 읽기 오류: {e}")
        return None


def calculate_atr(stock_code, data_folder="price_data", period=14):
    """ATR(Average True Range) 계산"""
    csv_path = os.path.join(data_folder, f"{stock_code}_daily_20days.csv")

    if not os.path.exists(csv_path):
        return None

    try:
        with open(csv_path, "r", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            rows = list(reader)[:period]

            if len(rows) < period:
                return None

            true_ranges = []
            for i in range(len(rows) - 1):
                high = float(rows[i]["high"])
                low = float(rows[i]["low"])
                prev_close = float(rows[i + 1]["close"])

                tr = max(high - low, abs(high - prev_close), abs(low - prev_close))
                true_ranges.append(tr)

            atr = sum(true_ranges) / len(true_ranges)
            return int(atr)

    except Exception as e:
        print(f"❌ ATR 계산 오류: {e}")
        return None


def get_balance(qry_tp="3"):
    deposit_inquiry = KiwoomDepositInquiry(is_mock=False)
    result = deposit_inquiry.get_deposit_info(qry_tp=qry_tp)
    return result


class RealtimeAutoTrader:
    def __init__(self, access_token, use_mock=False, test_mode=False):
        """실시간 자동매매 클래스"""
        self.access_token = access_token
        self.trader = StockTrader(access_token, use_mock)
        self.realtime_client = None
        self.position_client = None
        self.test_mode = test_mode

        # 저장 경로 설정
        self.trading_dir = Path("trading")
        self.history_dir = self.trading_dir / "history"
        self.trading_dir.mkdir(exist_ok=True)
        self.history_dir.mkdir(exist_ok=True)

        # 매매 조건 저장
        self.trading_conditions = {}
        # 매매 상태 추적 (체결시에만 업데이트)
        self.trading_status = {}
        # 체결 이력
        self.trading_history = []
        # 전일 종가 저장
        self.previous_close_prices = {}
        # 전일 종가 날짜 저장
        self.previous_close_dates = {}
        # 트레일링 스톱 정보
        self.trailing_stops = {}
        # 대기중인 주문 (체결 전)
        self.pending_orders = {}
        # [NEW] 분할 매도 진행 중인 비동기 태스크
        self.partial_sell_tasks = {}

        # 계좌 잔고
        self.account_balance = 0
        try:
            self.refresh_account_balance(qry_tp="3", save=True)
        except Exception:
            # 실패 시 테스트 모드 기본값 처리
            if self.test_mode:
                self.account_balance = 10_000_000
                self._save_account_balance()
        print(f"💵 초기 계좌 잔고: {self.account_balance:,}원")

        # 실시간 포지션 (모든 보유 종목)
        self.realtime_positions = {}
        # 종목별 할당 투자금 (신규 매수용)
        self.stock_budgets = {}  # {stock_code: allocated_amount}
        # [NEW] 종목별 매도 후 재매수 자금
        self.re_entry_funds: Dict[str, Optional[int]] = {} # {stock_code: sold_amount} # [NEW]

        # 명령 파일 경로
        self.command_file = self.trading_dir / "commands.json"

        # 상태 복원
        self._load_trading_status()
        self._load_account_balance()
        self._load_trailing_stops()
        self._load_re_entry_funds() # [NEW] 재매수 자금 복원

    async def _process_commands(self):
        """명령 파일 모니터링 및 처리 (백그라운드)"""
        while True:
            try:
                if self.command_file.exists():
                    with open(self.command_file, "r", encoding="utf-8") as f:
                        commands = json.load(f)

                    processed = []
                    for cmd in commands:
                        if cmd.get("status") == "pending":
                            success = await self._execute_command(cmd)
                            cmd["status"] = "completed" if success else "failed"
                            cmd["processed_at"] = datetime.now().isoformat()
                        processed.append(cmd)

                    # 완료된 명령 저장
                    with open(self.command_file, "w", encoding="utf-8") as f:
                        json.dump(processed, f, ensure_ascii=False, indent=2)

            except Exception as e:
                print(f"❌ 명령 처리 오류: {e}")

            await asyncio.sleep(2)  # 2초마다 확인

    async def _execute_command(self, cmd):
        """개별 명령 실행"""
        action = cmd.get("action")
        stock_code = cmd.get("stock_code")

        try:
            if action == "add_manual_stock":
                # 수동 보유 종목 추가 (이미 보유 중)
                print(f"\n🔔 명령 수신: {stock_code} 추가 (수동 보유 종목)")

                # 실시간 잔고에서 종목 정보 대기
                retry_count = 0
                while stock_code not in self.realtime_positions and retry_count < 10:
                    await asyncio.sleep(1)
                    retry_count += 1

                if stock_code not in self.realtime_positions:
                    print(f"❌ {stock_code}를 실시간 잔고에서 찾을 수 없습니다.")
                    return False

                # 종목 추가
                result = self.add_manual_stock_to_bot(
                    stock_code=stock_code,
                    sell_price=cmd.get("sell_price"),
                    stop_loss_rate=cmd.get("stop_loss_rate"),
                    sell_time=cmd.get("sell_time"),
                    partial_sell_rate=cmd.get("partial_sell_rate"),  # NEW
                    max_partial_sells=cmd.get("max_partial_sells"),  # NEW
                )

                if result:
                    # 실시간 구독 추가
                    await self.realtime_client.subscribe_stocks([stock_code])
                    print(f"✅ {stock_code} 실시간 구독 시작")

                return result

            elif action == "add_new_stock":
                # 신규 종목 추가 (아직 보유하지 않음)
                print(f"\n🔔 명령 수신: {stock_code} 추가 (신규 매수)")

                result = self.add_new_stock_to_bot(
                    stock_code=stock_code,
                    buy_price=cmd.get("buy_price"),
                    sell_price=cmd.get("sell_price"),
                    stop_loss_rate=cmd.get("stop_loss_rate"),
                    sell_time=cmd.get("sell_time"),
                    take_profit_rate=cmd.get("take_profit_rate"),
                    allocated_budget=cmd.get("allocated_budget"),
                    partial_sell_rate=cmd.get("partial_sell_rate"),  # NEW
                    max_partial_sells=cmd.get("max_partial_sells"),  # NEW
                )

                if result:
                    # 실시간 구독 추가
                    await self.realtime_client.subscribe_stocks([stock_code])
                    print(f"✅ {stock_code} 실시간 구독 시작")

                return result

            elif action == "remove_stock":
                print(f"\n🔔 명령 수신: {stock_code} 제거")

                if stock_code in self.trading_conditions:
                    del self.trading_conditions[stock_code]

                if stock_code in self.trading_status:
                    del self.trading_status[stock_code]

                if stock_code in self.trailing_stops:
                    del self.trailing_stops[stock_code]

                if stock_code in self.stock_budgets:
                    del self.stock_budgets[stock_code]

                # [NEW] 재매수 자금도 정리
                if stock_code in self.re_entry_funds:
                    del self.re_entry_funds[stock_code]
                    self._save_re_entry_funds() # [NEW]

                # [NEW] 분할 매도 태스크도 정리
                if stock_code in self.partial_sell_tasks:
                    self.partial_sell_tasks[stock_code].cancel()
                    del self.partial_sell_tasks[stock_code]

                self._save_trading_status()
                self._save_trailing_stops()

                print(f"✅ {stock_code} 제거 완료")
                return True

            elif action == "update_conditions":
                print(f"\n🔔 명령 수신: {stock_code} 조건 변경")

                if stock_code not in self.trading_conditions:
                    print(f"❌ {stock_code}를 찾을 수 없습니다.")
                    return False

                condition = self.trading_conditions[stock_code]

                if cmd.get("sell_price") is not None:
                    condition["sell_price"] = cmd["sell_price"]
                    print(f"   익절가: {cmd['sell_price']:,}원으로 변경")

                if cmd.get("stop_loss_rate") is not None:
                    condition["stop_loss_rate"] = cmd["stop_loss_rate"]

                    # 손절가 재계산
                    prev_close = self.previous_close_prices.get(stock_code)
                    if prev_close:
                        new_stop = int(prev_close * (1 + cmd["stop_loss_rate"] / 100))
                        condition["stop_loss_price"] = new_stop
                        print(f"   손절가: {new_stop:,}원으로 변경")

                if cmd.get("allocated_budget") is not None:
                    self.stock_budgets[stock_code] = cmd["allocated_budget"]
                    # [NEW] 할당 금액이 업데이트되면, 재매수 자금은 리셋 (할당 예산을 다시 기준으로 삼도록)
                    if stock_code in self.re_entry_funds:
                        del self.re_entry_funds[stock_code]
                        self._save_re_entry_funds()

                    print(f"   할당금액: {cmd['allocated_budget']:,}원으로 변경")

                # [NEW] 분할 매도 설정 업데이트
                if cmd.get("partial_sell_rate") is not None:
                    condition["partial_sell_rate"] = cmd["partial_sell_rate"]
                    print(f"   분할 매도 비율: {cmd['partial_sell_rate']}%로 변경")

                if cmd.get("max_partial_sells") is not None:
                    condition["max_partial_sells"] = cmd["max_partial_sells"]
                    print(f"   최대 분할 횟수: {cmd['max_partial_sells']}회로 변경")

                print(f"✅ {stock_code} 조건 변경 완료")
                return True

        except Exception as e:
            print(f"❌ 명령 실행 실패: {e}")
            import traceback

            traceback.print_exc()
            return False

        return False

    def _load_trading_status(self):
        """저장된 거래 상태 복원"""
        status_file = self.trading_dir / "trading_status.json"

        if status_file.exists():
            try:
                with open(status_file, "r", encoding="utf-8") as f:
                    data = json.load(f)
                    self.trading_status = data.get("status", {})
                    print(f"✅ 거래 상태 복원 완료: {len(self.trading_status)}개 종목")

                    for code, status in self.trading_status.items():
                        if status.get("position") == "bought":
                            managed = (
                                "🤖 봇관리"
                                if status.get("managed_by_bot", True)
                                else "👤 수동보유"
                            )
                            print(
                                f"   📦 {code}: 보유중 ({managed}, 매수가: {status.get('buy_executed_price', 0):,}원)"
                            )
            except Exception as e:
                print(f"⚠️  거래 상태 복원 실패: {e}")

    def _save_trading_status(self):
        """거래 상태 저장 (자동매매 + 수동 보유 종목 모두 포함)"""
        status_file = self.trading_dir / "trading_status.json"

        try:
            # 실시간 잔고에서 모든 보유 종목 추가
            all_status = dict(self.trading_status)

            for code, pos_info in self.realtime_positions.items():
                if pos_info["quantity"] > 0:
                    if code in all_status:
                        # 이미 있으면 실제 잔고 정보만 업데이트
                        all_status[code].update(
                            {
                                "current_price": pos_info["current_price"],
                                "actual_quantity": pos_info["quantity"],
                                "actual_avg_price": pos_info["avg_price"],
                            }
                        )
                    else:
                        # 수동 보유 종목 추가
                        all_status[code] = {
                            "position": "bought",
                            "managed_by_bot": False,
                            "buy_executed_price": pos_info["avg_price"],
                            "buy_executed_quantity": pos_info["quantity"],
                            "current_price": pos_info["current_price"],
                            "actual_quantity": pos_info["quantity"],
                            "actual_avg_price": pos_info["avg_price"],
                        }

            data = {
                "updated_at": datetime.now().isoformat(),
                "status": all_status,
            }

            with open(status_file, "w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False, indent=2)

        except Exception as e:
            print(f"❌ 거래 상태 저장 실패: {e}")

    def refresh_account_balance(self, qry_tp="3", save: bool = True) -> int:
        """
        예수금 조회로 계좌잔고를 갱신.
        - get_balance() 반환값을 안전히 int로 변환해 self.account_balance에 저장
        - save=True이면 history/account_balance.json에 저장
        - 반환값: 갱신된 잔고 (int)
        """
        try:
            bal = get_balance(qry_tp=qry_tp)
            if bal is None:
                print("⚠️ 예수금 조회 실패: 잔고를 갱신하지 않습니다.")
                return self.account_balance
            # 안전 변환
            try:
                bal_int = int(bal)
            except Exception:
                bal_int = int(float(str(bal).strip()))
            old = getattr(self, "account_balance", 0)
            self.account_balance = bal_int
            if save:
                self._save_account_balance()
            print(f"💵 계좌 잔고 업데이트: {self.account_balance:,}원 (이전: {old:,})")
            return self.account_balance
        except Exception as e:
            print(f"❌ 잔고 갱신 실패: {e}")
            return getattr(self, "account_balance", 0)

    def _load_account_balance(self):
        """계좌 잔고 복원"""
        balance_file = self.history_dir / "account_balance.json"

        if balance_file.exists():
            try:
                with open(balance_file, "r", encoding="utf-8") as f:
                    data = json.load(f)
                    self.account_balance = data.get("balance", 0)
                    print(f"💵 계좌 잔고: {self.account_balance:,}원")
            except Exception as e:
                print(f"⚠️  잔고 복원 실패: {e}")
                self.account_balance = 0
        else:
            if self.test_mode:
                self.account_balance = 10000000
                self._save_account_balance()
                print(f"💵 초기 계좌 잔고: {self.account_balance:,}원")

    def _save_account_balance(self):
        """계좌 잔고 저장"""
        balance_file = self.history_dir / "account_balance.json"

        try:
            data = {
                "updated_at": datetime.now().isoformat(),
                "balance": self.account_balance,
            }
            with open(balance_file, "w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
        except Exception as e:
            print(f"❌ 잔고 저장 실패: {e}")

    def _load_trailing_stops(self):
        """트레일링 스톱 정보 복원"""
        trailing_file = self.trading_dir / "trailing_stops.json"

        if trailing_file.exists():
            try:
                with open(trailing_file, "r", encoding="utf-8") as f:
                    self.trailing_stops = json.load(f)
                    print(f"✅ 트레일링 스톱 복원: {len(self.trailing_stops)}개 종목")

                    for code, data in self.trailing_stops.items():
                        print(
                            f"   🎯 {code}: 최고가 {data['highest']:,}원, 스톱 {data['stop_price']:,}원"
                        )
            except Exception as e:
                print(f"⚠️  트레일링 스톱 복원 실패: {e}")

    def _save_trailing_stops(self):
        """트레일링 스톱 정보 저장"""
        trailing_file = self.trading_dir / "trailing_stops.json"

        try:
            with open(trailing_file, "w", encoding="utf-8") as f:
                json.dump(self.trailing_stops, f, ensure_ascii=False, indent=2)
        except Exception as e:
            print(f"❌ 트레일링 스톱 저장 실패: {e}")
            
    # [NEW] 재매수 자금 복원 및 저장 메서드 추가
    def _load_re_entry_funds(self):
        """저장된 재매수 자금 정보 복원"""
        funds_file = self.trading_dir / "re_entry_funds.json"

        if funds_file.exists():
            try:
                with open(funds_file, "r", encoding="utf-8") as f:
                    data = json.load(f)
                    self.re_entry_funds = {
                        k: v if v is not None else None
                        for k, v in data.get("funds", {}).items()
                    }
                    print(f"✅ 재매수 자금 복원 완료: {len(self.re_entry_funds)}개 종목")

                    for code, fund in self.re_entry_funds.items():
                        if fund is not None:
                            print(f"   💰 {code}: 재매수 자금 {fund:,}원")
            except Exception as e:
                print(f"⚠️  재매수 자금 복원 실패: {e}")

    def _save_re_entry_funds(self):
        """재매수 자금 정보 저장"""
        funds_file = self.trading_dir / "re_entry_funds.json"

        try:
            # None 값은 JSON에서 null로 저장되도록 그대로 둡니다.
            data = {
                "updated_at": datetime.now().isoformat(),
                "funds": self.re_entry_funds,
            }
            with open(funds_file, "w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
        except Exception as e:
            print(f"❌ 재매수 자금 저장 실패: {e}")
    # [NEW] 재매수 자금 복원 및 저장 메서드 추가 끝

    def _post_trade_fund_management(self, trade_data: Dict): # [NEW]
        """매수/매도 체결 후 재매수 자금 업데이트 및 리셋"""
        stock_code = trade_data["stock_code"]
        action = trade_data["action"]
        
        if action == "sell":
            # 매도 체결 시: 재매수 자금 업데이트
            # 체결가와 수량으로 매도 금액 계산 (수수료, 세금 제외)
            executed_amount = trade_data["price"] * trade_data["quantity"]

            # 기존 할당 금액이 있거나, 이미 재매수 자금으로 관리 중인 경우
            if stock_code in self.stock_budgets or stock_code in self.re_entry_funds:
                self.re_entry_funds[stock_code] = executed_amount
                print(f"💰 [{stock_code}] 매도 체결: 재매수 자금 {executed_amount:,}원 업데이트.")
                self._save_re_entry_funds()
                
        elif action == "buy":
            # 매수 체결 시: 재매수 자금 리셋
            if stock_code in self.re_entry_funds:
                # 매수 체결이 확인되면 재매수 자금 정보를 초기화
                del self.re_entry_funds[stock_code]
                print(f"💰 [{stock_code}] 매수 체결: 재매수 자금 리셋 완료.")
                self._save_re_entry_funds()

    def _save_trade_history(self, trade_data):
        """체결 이력 저장 (JSON, CSV)"""
        today = date.today().isoformat()
        
        # [NEW] 매수/매도 체결 후 재매수 자금 업데이트/리셋
        self._post_trade_fund_management(trade_data) # [NEW]

        # JSON 저장
        json_file = self.history_dir / f"trades_{today}.json"
        try:
            trades = []
            if json_file.exists():
                with open(json_file, "r", encoding="utf-8") as f:
                    trades = json.load(f)

            trades.append(trade_data)

            with open(json_file, "w", encoding="utf-8") as f:
                json.dump(trades, f, ensure_ascii=False, indent=2)
        except Exception as e:
            print(f"❌ JSON 이력 저장 실패: {e}")

        # CSV 저장
        csv_file = self.history_dir / f"trades_{today}.csv"
        try:
            file_exists = csv_file.exists()

            with open(csv_file, "a", encoding="utf-8", newline="") as f:
                fieldnames = [
                    "거래일시",
                    "종목코드",
                    "구분",
                    "체결가",
                    "수량",
                    "손익",
                    "손익률",
                    "잔고",
                    "주문번호",
                ]
                writer = csv.DictWriter(f, fieldnames=fieldnames)

                if not file_exists:
                    writer.writeheader()

                writer.writerow(
                    {
                        "거래일시": trade_data["time"],
                        "종목코드": trade_data["stock_code"],
                        "구분": trade_data["action"],
                        "체결가": trade_data["price"],
                        "수량": trade_data["quantity"],
                        "손익": trade_data.get("profit", ""),
                        "손익률": (
                            f"{trade_data.get('profit_rate', '')}%"
                            if trade_data.get("profit_rate")
                            else ""
                        ),
                        "잔고": trade_data.get("balance", self.account_balance),
                        "주문번호": trade_data["order_no"],
                    }
                )
        except Exception as e:
            print(f"❌ CSV 이력 저장 실패: {e}")

    def _calculate_buy_quantity(self, stock_code, current_price):
        """매수 수량 계산 (손실률 기반)"""
        condition = self.trading_conditions.get(stock_code)
        if not condition:
            print(f"❌ [{stock_code}] 매매 조건을 찾을 수 없습니다.")
            return 0

        if self.account_balance <= 0:
            print(f"❌ [{stock_code}] 계좌 잔고가 0원입니다.")
            return 0

        stop_loss_rate = abs(condition.get("stop_loss_rate", 3.0))
        is_market_close_buy = condition.get("buy_price") is None

        # [MODIFIED] allocated_budget 대신 재매수 자금 (re_entry_fund) 사용
        re_entry_fund = self.re_entry_funds.get(stock_code)
        allocated_budget = self.stock_budgets.get(stock_code)
        
        # 투자 금액 결정: 재매수 자금 > 할당금액 > 계좌잔고 기반 순
        target_fund = None
        if re_entry_fund is not None:
            target_fund = re_entry_fund
            print(f"   자금 출처: 매도 후 재매수 자금 ({target_fund:,}원)") # [MODIFIED]
        elif allocated_budget is not None:
            target_fund = allocated_budget
            print(f"   자금 출처: 초기 할당금액 ({target_fund:,}원)") # [MODIFIED]
        else:
            target_fund = self.account_balance
            print(f"   자금 출처: 계좌 잔고 기반") # [MODIFIED]

        if target_fund <= 0:
            print(f"❌ [{stock_code}] 투자 가능 자금이 0원입니다.")
            return 0
            
        # target_fund 기반으로 max_loss_amount 계산
        if target_fund != self.account_balance:
            max_loss_amount = int(target_fund * 0.015)
        else:
            max_loss_amount = self.account_balance * 0.015

        if stop_loss_rate >= 3.0:
            base_ratio = 0.50 if not is_market_close_buy else 0.45
        elif stop_loss_rate >= 2.0:
            base_ratio = 0.75 if not is_market_close_buy else 0.70
        else:
            base_ratio = 1.00 if not is_market_close_buy else 0.90

        # [MODIFIED] target_fund 기반으로 max_investment 계산
        max_investment = int(target_fund * base_ratio)


        stop_loss_rate_value = condition.get("stop_loss_rate", -3.0)
        expected_stop_loss_price = int(current_price * (1 + stop_loss_rate_value / 100))
        loss_per_share = current_price - expected_stop_loss_price

        if loss_per_share <= 0:
            print(f"❌ 손절률 설정 오류 (양수여야 함)")
            return 0

        max_quantity_by_loss = int(max_loss_amount / loss_per_share)
        max_quantity_by_balance = int(max_investment / current_price)

        quantity = min(max_quantity_by_loss, max_quantity_by_balance)

        print(f"📊 매수 수량 계산:")
        print(f"   예상 매수가: {current_price:,}원")
        print(
            f"   예상 손절가: {expected_stop_loss_price:,}원 ({stop_loss_rate_value:+.1f}%)"
        )
        print(f"   주당 예상 손실: {loss_per_share:,}원")
        print(f"   손절률: {stop_loss_rate:.1f}% → 투자비율: {base_ratio*100:.0f}%")
        print(f"   계좌잔고: {self.account_balance:,}원")
        print(f"   최대투자: {max_investment:,}원 (기준금액: {target_fund:,}원)") # [MODIFIED]
        print(
            f"   ✅ 최종 매수수량: {quantity}주 (투자금: {quantity * current_price:,}원)"
        )

        return quantity

    def add_manual_stock_to_bot(
        self,
        stock_code,
        sell_price=None,
        stop_loss_rate=None,
        sell_time=None,
        partial_sell_rate=None,  # NEW
        max_partial_sells=None,  # NEW
    ):
        """수동으로 보유 중인 종목을 자동매매에 추가"""

        # 🛡️ 중복 등록 검사 — 이미 'bought' 상태인 경우 처리
        existing_status = self.trading_status.get(stock_code)
        if existing_status and existing_status.get("position") == "bought":
            if existing_status.get("managed_by_bot") is False:
                # 이미 수동보유로 등록됨 → 조건만 업데이트
                print(f"ℹ️  [{stock_code}] 이미 수동보유로 등록되어 있습니다.")

                # 매매 조건 업데이트
                if stock_code in self.trading_conditions:
                    condition = self.trading_conditions[stock_code]

                    if sell_price is not None and sell_price != condition.get(
                        "sell_price"
                    ):
                        condition["sell_price"] = sell_price
                        print(f"   📝 익절가 업데이트: {sell_price:,}원")

                    if stop_loss_rate is not None and stop_loss_rate != condition.get(
                        "stop_loss_rate"
                    ):
                        condition["stop_loss_rate"] = stop_loss_rate
                        # 손절가 재계산
                        previous_close = self.previous_close_prices.get(stock_code)
                        if previous_close:
                            new_stop = int(previous_close * (1 + stop_loss_rate / 100))
                            condition["stop_loss_price"] = new_stop
                            print(
                                f"   📝 손절가 업데이트: {new_stop:,}원 ({stop_loss_rate:+.1f}%)"
                            )

                    if sell_time is not None and sell_time != condition.get(
                        "sell_time"
                    ):
                        condition["sell_time"] = sell_time
                        print(f"   📝 매도시간 업데이트: {sell_time}")

                    if partial_sell_rate is not None:
                        condition["partial_sell_rate"] = partial_sell_rate
                        print(f"   📝 분할 매도 비율 업데이트: {partial_sell_rate}%")

                    if max_partial_sells is not None:
                        condition["max_partial_sells"] = max_partial_sells
                        print(f"   📝 최대 분할 횟수 업데이트: {max_partial_sells}회")

                    self._save_trading_status()
                    print(f"   ✅ 조건 업데이트 완료")
                    return True
                else:
                    print(f"   ⚠️  매매 조건이 없어 그대로 유지합니다.")
                    return True
            else:
                # 봇이 자동 매수한 종목
                print(f"⚠️  [{stock_code}] 이미 봇이 자동매수한 종목입니다.")
                print(f"   현재 수량: {existing_status.get('actual_quantity', 0)}주")
                print(f"   평단가: {existing_status.get('actual_avg_price', 0):,}원")
                print(f"   💡 조건 변경은 update_conditions()를 사용하세요.")
                return False

        # 실시간 잔고에서 확인
        if stock_code not in self.realtime_positions:
            print(f"❌ [{stock_code}] 실시간 잔고에서 찾을 수 없습니다.")
            print(f"   현재 보유 종목: {list(self.realtime_positions.keys())}")
            print(f"   💡 잔고가 업데이트될 때까지 잠시 기다려주세요 (최대 10초)")
            return False

        pos_info = self.realtime_positions[stock_code]

        if pos_info["quantity"] <= 0:
            print(f"❌ [{stock_code}] 보유 수량이 0입니다.")
            return False

        previous_close = load_previous_close(stock_code)
        if previous_close:
            self.previous_close_prices[stock_code] = previous_close

        stop_loss_price = None
        if stop_loss_rate and previous_close:
            stop_loss_price = int(previous_close * (1 + stop_loss_rate / 100))

        self.trading_conditions[stock_code] = {
            "buy_price": None,
            "sell_price": sell_price,
            "stop_loss_price": stop_loss_price,
            "stop_loss_rate": stop_loss_rate,
            "sell_time": sell_time,
            "partial_sell_rate": (
                partial_sell_rate if partial_sell_rate is not None else 50.0
            ),
            "max_partial_sells": (
                max_partial_sells if max_partial_sells is not None else 2
            ),
        }

        self.trading_status[stock_code] = {
            "position": "bought",
            "managed_by_bot": False,
            "buy_executed_price": pos_info["avg_price"],
            "buy_executed_quantity": pos_info["quantity"],
            "actual_quantity": pos_info["quantity"],
            "actual_avg_price": pos_info["avg_price"],
            "current_price": pos_info["current_price"],
        }

        self._save_trading_status()

        print(f"\n✅ [{stock_code}] 수동 보유 종목을 자동매매에 추가했습니다!")
        print(f"   종목명: {pos_info['stock_name']}")
        print(f"   보유수량: {pos_info['quantity']}주")
        print(f"   평단가: {pos_info['avg_price']:,}원")
        if sell_price:
            print(f"   익절가: {sell_price:,}원")
        else:
            print(f"   익절가: 트레일링 스톱")
        if stop_loss_price:
            print(f"   손절가: {stop_loss_price:,}원")
        print(
            f"   분할 매도: {self.trading_conditions[stock_code]['partial_sell_rate']}% x {self.trading_conditions[stock_code]['max_partial_sells']}회"
        )

        return True

    def add_new_stock_to_bot(
        self,
        stock_code,
        buy_price=None,
        sell_price=None,
        stop_loss_rate=None,
        sell_time=None,
        take_profit_rate=None,
        allocated_budget=None,
        partial_sell_rate=None,  # NEW
        max_partial_sells=None,  # NEW
    ):
        """
        신규 종목을 자동매매에 추가 (아직 보유하지 않은 종목)

        Args:
            stock_code: 종목코드
            buy_price: 매수가 (None이면 종가매수)
            sell_price: 익절가 (None이면 트레일링 스톱)
            stop_loss_rate: 손절률 (예: -3.0)
            sell_time: 매도 시간 (예: "14:50")
            take_profit_rate: 익절률 (예: 4.0)
            allocated_budget: 할당 투자금액 (예: 1000000원)
            partial_sell_rate: 분할 매도 비율 (예: 30.0)
            max_partial_sells: 최대 분할 횟수 (예: 3)
        """

        """
        신규 종목을 자동매매에 추가.
        - 이미 'bought' 상태(실제로 보유 중)인 종목에 대해서는 추가를 거부합니다.
        - 조건만 존재하거나 상태가 'none'이면 안전하게 업데이트(덮어쓰기)합니다.
        """
        # 🛡️ 보호: 이미 실보유(포지션 'bought')이면 실수로 덮어쓰지 않음
        existing_status = self.trading_status.get(stock_code)
        if existing_status and existing_status.get("position") == "bought":
            print(f"❌ [{stock_code}] 이미 보유중인 종목입니다!")
            print(f"   현재 수량: {existing_status.get('actual_quantity', 0)}주")
            print(f"   평단가: {existing_status.get('actual_avg_price', 0):,}원")
            print(f"   💡 수동 보유 종목은 add_manual_stock_to_bot() 사용하세요.")
            return False

        # 🔄 기존 조건이 있으면 경고 (대기중 상태는 업데이트 허용)
        if stock_code in self.trading_conditions:
            existing_pos = (
                existing_status.get("position") if existing_status else "none"
            )
            if existing_pos == "none":
                print(
                    f"⚠️  [{stock_code}] 기존 매수 대기 조건이 있습니다. 새 조건으로 업데이트합니다."
                )
            else:
                print(f"⚠️  [{stock_code}] 기존 매매 조건을 덮어씁니다.")

        # 전일 종가 로드
        previous_close = load_previous_close(stock_code)

        if not previous_close:
            if self.test_mode:
                previous_close = 50000
                print(f"🧪 테스트 모드: 가상 전일종가 {previous_close:,}원")
            else:
                print(f"❌ [{stock_code}] 전일 종가를 가져올 수 없습니다.")
                return False

        self.previous_close_prices[stock_code] = previous_close

        # 익절가 계산
        if take_profit_rate and not sell_price:
            sell_price = int(previous_close * (1 + take_profit_rate / 100))

        # 손절가 계산
        stop_loss_price = None
        if stop_loss_rate:
            stop_loss_price = int(previous_close * (1 + stop_loss_rate / 100))

        # 매매 조건 추가
        self.trading_conditions[stock_code] = {
            "buy_price": buy_price,
            "sell_price": sell_price,
            "stop_loss_price": stop_loss_price,
            "stop_loss_rate": stop_loss_rate,
            "sell_time": sell_time,
            "partial_sell_rate": (
                partial_sell_rate if partial_sell_rate is not None else 50.0
            ),
            "max_partial_sells": (
                max_partial_sells if max_partial_sells is not None else 2
            ),
        }

        # 할당 금액 저장
        if allocated_budget:
            self.stock_budgets[stock_code] = allocated_budget
            # [NEW] 신규 종목 추가 시 재매수 자금 리셋
            if stock_code in self.re_entry_funds:
                del self.re_entry_funds[stock_code]
                self._save_re_entry_funds()

        # 상태 초기화
        self.trading_status[stock_code] = {
            "position": "none",
            "managed_by_bot": True,
            "buy_executed_price": None,
            "buy_executed_quantity": 0,
            "current_price": None,
        }

        self._save_trading_status()

        print(f"\n✅ [{stock_code}] 신규 종목을 자동매매에 추가했습니다!")
        print(f"   전일종가: {previous_close:,}원")
        if allocated_budget:
            print(f"   할당금액: {allocated_budget:,}원")
        if buy_price:
            print(f"   매수가: {buy_price:,}원 (지정가)")
        else:
            print(f"   매수가: 종가매수 (15:20 이후)")
        if sell_price:
            if take_profit_rate:
                print(f"   익절가: {sell_price:,}원 (+{take_profit_rate}%)")
            else:
                print(f"   익절가: {sell_price:,}원")
        else:
            print(f"   익절가: 트레일링 스톱")
        if stop_loss_price:
            print(f"   손절가: {stop_loss_price:,}원 ({stop_loss_rate:+.1f}%)")
        print(
            f"   분할 매도: {self.trading_conditions[stock_code]['partial_sell_rate']}% x {self.trading_conditions[stock_code]['max_partial_sells']}회"
        )

        return True

    def add_trading_condition(
        self,
        stock_code,
        buy_price=None,
        sell_price=None,
        stop_loss_rate=None,
        sell_time=None,
        previous_close=None,
        take_profit_rate=None,
        allocated_budget=None,
        partial_sell_rate=None,  # NEW
        max_partial_sells=None,  # NEW
    ):
        """매매 조건 추가"""
        if previous_close is None:
            previous_close = load_previous_close(stock_code)

            if previous_close is None and self.test_mode:
                previous_close = RealtimeStockClient.TEST_STOCK_PRICES.get(
                    stock_code, 50000
                )

        if previous_close:
            self.previous_close_prices[stock_code] = previous_close

        if take_profit_rate and stock_code in self.previous_close_prices:
            prev_close = self.previous_close_prices[stock_code]
            sell_price = int(prev_close * (1 + take_profit_rate / 100))

        stop_loss_price = None
        if stop_loss_rate and stock_code in self.previous_close_prices:
            prev_close = self.previous_close_prices[stock_code]
            stop_loss_price = int(prev_close * (1 + stop_loss_rate / 100))

        self.trading_conditions[stock_code] = {
            "buy_price": buy_price,
            "sell_price": sell_price,
            "stop_loss_price": stop_loss_price,
            "stop_loss_rate": stop_loss_rate,
            "sell_time": sell_time,
            "partial_sell_rate": (
                partial_sell_rate if partial_sell_rate is not None else 50.0
            ),
            "max_partial_sells": (
                max_partial_sells if max_partial_sells is not None else 2
            ),
        }

        # 할당 금액 저장
        if allocated_budget:
            self.stock_budgets[stock_code] = allocated_budget
            # [NEW] 신규 종목 추가 시 재매수 자금 리셋
            if stock_code in self.re_entry_funds:
                del self.re_entry_funds[stock_code]
                self._save_re_entry_funds()

        if stock_code not in self.trading_status:
            self.trading_status[stock_code] = {
                "position": "none",
                "managed_by_bot": True,
                "buy_executed_price": None,
                "buy_executed_quantity": 0,
                "current_price": None,
            }

        mode_str = "🧪 테스트" if self.test_mode else "💰 실전"
        print(f"\n{mode_str} [{stock_code}] 매매 조건 설정 완료")
        if previous_close:
            print(f"   전일종가: {previous_close:,}원")
        if allocated_budget:
            print(f"   할당금액: {allocated_budget:,}원")
        if buy_price:
            print(f"   매수가: {buy_price:,}원 (지정가)")
        else:
            print(f"   매수가: 종가매수 (15:20 이후 시장가)")
        if sell_price:
            if take_profit_rate:
                print(f"   익절가: {sell_price:,}원 (전일종가 +{take_profit_rate}%)")
            else:
                print(f"   익절가: {sell_price:,}원")
        else:
            print(f"   익절가: 트레일링 스톱 (동적 조정)")
        if stop_loss_price:
            print(
                f"   손절가: {stop_loss_price:,}원 (전일종가 대비 {stop_loss_rate:+.1f}%)"
            )
        print(
            f"   분할 매도: {self.trading_conditions[stock_code]['partial_sell_rate']}% x {self.trading_conditions[stock_code]['max_partial_sells']}회"
        )

    def get_all_positions(self):
        """전체 보유 종목 정보 조회"""
        print("\n" + "=" * 70)
        print("💼 전체 포트폴리오 현황")
        print("=" * 70)
        if not self.realtime_positions:
            print("실시간 잔고 정보가 없습니다.")
            print("=" * 70)
            return

        total_eval_amount = 0
        total_profit = 0
        tracked_count = 0
        other_count = 0

        print("\n🎯 자동매매 추적 종목:")
        for code, pos_info in sorted(self.realtime_positions.items()):
            if pos_info["quantity"] <= 0:
                continue
            if code in self.trading_conditions:
                tracked_count += 1
                self._print_position_info(code, pos_info)
                total_eval_amount += pos_info["eval_amount"]
                total_profit += pos_info.get("profit", 0)

        if tracked_count == 0:
            print(" (없음)")

        print("\n📊 기타 보유 종목:")
        for code, pos_info in sorted(self.realtime_positions.items()):
            if pos_info["quantity"] <= 0:
                continue
            if code not in self.trading_conditions:
                other_count += 1
                self._print_position_info(code, pos_info)
                total_eval_amount += pos_info["eval_amount"]
                total_profit += pos_info.get("profit", 0)

        if other_count == 0:
            print(" (없음)")

        print("\n" + "-" * 70)
        print(f"📊 총 보유 종목: {tracked_count + other_count}개")
        print(f" 🎯 추적 중: {tracked_count}개")
        print(f" 📊 기타: {other_count}개")
        print(f"💰 총 평가금액: {total_eval_amount:,}원")
        profit_emoji = "🎉" if total_profit >= 0 else "😢"
        print(f"{profit_emoji} 총 평가손익: {total_profit:+,}원")
        print(f"💵 예수금 잔고: {self.account_balance:,}원")
        print(f"🏦 총 자산: {self.account_balance + total_eval_amount:,}원")
        print("=" * 70)

    def _print_position_info(self, code, pos_info):
        """종목 정보 출력 헬퍼"""
        stock_name = pos_info["stock_name"]
        quantity = pos_info["quantity"]
        avg_price = pos_info["avg_price"]
        current_price = pos_info["current_price"]
        eval_amount = pos_info["eval_amount"]
        profit = pos_info.get("profit", 0)
        profit_rate = pos_info.get("profit_rate", 0.0)
        profit_emoji = "🎉" if profit >= 0 else "😢"
        print(f"\n   {stock_name}({code})")
        print(f"   보유: {quantity}주")
        print(f"   평단가: {avg_price:,}원 | 현재가: {current_price:,}원")
        print(f"   평가액: {eval_amount:,}원")
        print(f"   {profit_emoji} 평가손익: {profit:+,}원 ({profit_rate:+.2f}%)")

    async def on_price_update(self, stock_code, values):
        """실시간 주식체결 콜백"""
        if stock_code not in self.trading_conditions:
            return

        try:
            current_price = int(values.get("10", 0))
        except (ValueError, TypeError):
            return

        if current_price == 0:
            return

        self.trading_status[stock_code]["current_price"] = current_price
        condition = self.trading_conditions[stock_code]
        status = self.trading_status[stock_code]
        timestamp = datetime.now().strftime("%H:%M:%S")

        try:
            change_rate = float(values.get("12", "0"))
        except (ValueError, TypeError):
            change_rate = 0.0

        change_rate_str = f"{change_rate:+.2f}%" if change_rate != 0 else "0.00%"
        position_str = "📈 보유중" if status["position"] == "bought" else "💤 대기중"
        managed_str = "" if status.get("managed_by_bot", True) else " [수동보유]"

        print(
            f"[{timestamp}] {stock_code} | 현재가: {current_price:,}원 ({change_rate_str}) | {position_str}{managed_str}"
        )

        # 매수 로직
        if status["position"] == "none" and stock_code not in self.pending_orders:
            should_buy = False

            if condition["buy_price"] is None:
                now = datetime.now()
                if now.hour == 15 and now.minute >= 20:
                    should_buy = True
                elif self.test_mode:
                    should_buy = True
            elif current_price <= condition["buy_price"]:
                should_buy = True

            if should_buy:
                quantity = self._calculate_buy_quantity(stock_code, current_price)

                if quantity > 0 and self.account_balance >= current_price * quantity:
                    self.pending_orders[stock_code] = {
                        "type": "buy",
                        "quantity": quantity,
                    }
                    await self._execute_buy(stock_code, quantity, current_price)

        # 매도 로직
        elif status["position"] == "bought" and stock_code not in self.pending_orders:
            sell_reason = None
            quantity = status.get(
                "actual_quantity", status.get("buy_executed_quantity", 0)
            )

            if quantity == 0:
                return

            buy_price = status.get("actual_avg_price", status["buy_executed_price"])

            if condition["sell_price"] is None:
                self._update_trailing_stop(stock_code, current_price)
                if stock_code in self.trailing_stops:
                    if current_price <= self.trailing_stops[stock_code]["stop_price"]:
                        sell_reason = "트레일링스톱"

            if (
                not sell_reason
                and condition["sell_price"]
                and current_price >= condition["sell_price"]
            ):
                sell_reason = "익절"

            if (
                not sell_reason
                and condition["stop_loss_price"]
                and current_price <= condition["stop_loss_price"]
            ):
                sell_reason = "손절"

            if (
                not sell_reason
                and condition["sell_time"]
                and condition["sell_time"] != "15:30"
            ):
                now = datetime.now()
                try:
                    target_hour, target_minute = map(
                        int, condition["sell_time"].split(":")
                    )
                    if now.hour == target_hour and now.minute == target_minute:
                        sell_reason = "예약시간"
                except ValueError:
                    pass

            if sell_reason:
                max_sells = condition.get("max_partial_sells", 2)  # 기본값 2회

                # 1. 분할 매도 조건 확인 (횟수가 1보다 큰 경우 분할 매도로 처리)
                is_partial_sell_active = max_sells > 1

                # 🛡️ 주문 중복 방지 확인
                if stock_code in self.partial_sell_tasks:
                    print(
                        f" ℹ️ [{stock_code}] 분할 매도 주문이 이미 진행 중입니다. 새로운 주문을 무시합니다."
                    )
                    return

                # 2. 실행 로직 분기
                if is_partial_sell_active:
                    # 분할 매도 실행
                    print(
                        f" 🔔 매도 조건 발생 (사유: {sell_reason}). 분할 매도 태스크 시작."
                    )
                    self.pending_orders[stock_code] = {"type": "sell_in_progress"}
                    task = asyncio.create_task(
                        self._handle_partial_sell(
                            stock_code, current_price, sell_reason
                        )
                    )
                    self.partial_sell_tasks[stock_code] = task
                else:
                    # 전량 매도 실행 (분할 매도 조건이 없거나 max_sells=1일 때)
                    print(
                        f" 🔔 매도 조건 발생 (사유: {sell_reason}). 전량 매도 주문 시작."
                    )
                    # 현재 보유 수량 전체를 매도
                    quantity_to_sell = status.get(
                        "actual_quantity", status.get("buy_executed_quantity", 0)
                    )

                    if quantity_to_sell > 0:
                        # 일반 매도 (is_partial=False)
                        self.pending_orders[stock_code] = {
                            "type": "sell",
                            "quantity": quantity_to_sell,
                        }
                        await self._execute_sell(
                            stock_code,
                            quantity_to_sell,
                            current_price,
                            sell_reason,
                            is_partial=False,
                        )
                    else:
                        print(
                            f" ❌ [{stock_code}] 보유 수량이 0이라 매도할 수 없습니다."
                        )

    async def _handle_partial_sell(self, stock_code, current_price, sell_reason):
        """시간 간격을 두고 반복적으로 분할 매도 주문을 실행"""
# ... (rest of the code is unchanged)