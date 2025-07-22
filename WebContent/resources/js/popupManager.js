// 공통 팝업 모듈
const PopupManager = {
    // 팝업 HTML을 동적으로 로드하고 표시
    open: function(popupId, url, data = {}) {
        const existingPopup = document.getElementById(popupId);
        if (existingPopup) {
            existingPopup.remove();
        }

        
        // Ajax로 팝업 HTML 로드
        $.ajax({
            url: url,
            type: 'GET',
            data: data,
            cache: false,
            contentType: 'text/html; charset=UTF-8',
            beforeSend: function(xhr) {
                xhr.overrideMimeType('text/html; charset=UTF-8');
            },
            success: function(html) {
                // 팝업 HTML을 body에 추가
                $('body').append(html);
                
                // 팝업 표시
                const popup = document.getElementById(popupId);
                if (popup) {
                    popup.style.display = 'block';
                    
                    // 팝업 닫기 이벤트 바인딩
                    PopupManager.bindCloseEvents(popupId);
                }
            },
            error: function(xhr, status, error) {
                console.error('팝업 로드 실패:', error);
                alert('팝업을 불러오는 중 오류가 발생했습니다.');
            }
        });
    },

    // 팝업 닫기
    close: function(popupId) {
        const popup = document.getElementById(popupId);
        if (popup) {
            popup.style.display = 'none';
            popup.remove();
        }
    },

    // 팝업 닫기 이벤트 바인딩
    bindCloseEvents: function(popupId) {
        const popup = document.getElementById(popupId);
        if (!popup) return;

        // X 버튼 클릭 시 닫기
        const closeBtn = popup.querySelector('.btn-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', function() {
                PopupManager.close(popupId);
            });
        }

        // 취소 버튼 클릭 시 닫기
        const cancelBtn = popup.querySelector('.btn.close');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', function() {
                PopupManager.close(popupId);
            });
        }

        // 오버레이 클릭 시 닫기 (선택사항)
        popup.addEventListener('click', function(e) {
            if (e.target === popup) {
                PopupManager.close(popupId);
            }
        });
    }
    

};

// 커스텀 팝업 호출 함수 (매개변수 지원)
function openCustomPopup(popupId, url, data = {}) {
    PopupManager.open(popupId, url, data);
}

