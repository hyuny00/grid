package com.futechsoft.framework.exception;

public enum ErrorCode {



	ACCESS_DENIED("A001", "Access is Denied"),
    FAIL_SAVE_USER("U000", "사용자등록에 실패했습니다."),

    DISABLED_USER("U001", "계정이 비활성화 되었습니다."),
    ACCOUNT_EXPIRED_USER("U002", "계정유효기간이 만료되었습니다."),
    CREDENTIALS_EXPIRED_USER("U003", "비밀번호 유효기간이 만료되었습니다."),
    LOCKED_USER("U004", "계정이 잠김 상태입니다"),
    BAD_CREDENTIALS("U005", "ID와 비밀번호가 맞지않습니다"),
    AUTHENTICATION_CREDENTIALS_NOT_FOUND("U006", "인증요구가 거부되었습니다."),
    USER_NOT_FOUND("U007", "사용자 정보가 없습니다. ID로 로그인해서 인증서를 등록하세요."),
    USER_DUP_FOUND("U008", "사용자 DN정보가 중복되어있거나 잘못되었습니다. 관리자에게 문의 하세요."),
    SSO_USER_NOT_FOUND("U009", "사용자 정보가 없습니다. 로그인해서 아름터아이디를 등록하세요."),

    FILE_SIZE_ERROR("FILE-001", "파일 크기는 {{fileSize}} 이하로 제한됩니다."),
    FILE_EXT_ERROR("FILE-002", "파일 확장자오류."),
    FILE_UPLOAD_ERROR("FILE-03", "파일 업로드 오류"),
    FILE_SAVE_ERROR("FILE-04", "파일 저장 오류"),
    FILE_NOT_FOUND("FILE-05", "해당파일이 존재하지 않습니다."),
    FILE_ACCEPT_ERROR("FILE-06", " 파일확장자가 {{fileExt}}인 파일은 업로드 할수 없습니다."),
    FILE_ACCESS_DENIED("FILE-07", " 파일을 다운로드할 권한이 없습니다."),
    FILE_DELETE_DENIED("FILE-08", " 파일을 삭제할 권한이 없습니다."),
    ;

    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.message = message;
        this.code = code;
    }

    public String getCode() {
    	return code;
    }

    public String getMessage() {
    	return message;
    }

}
