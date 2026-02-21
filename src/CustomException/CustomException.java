// CustomException.java
package CustomException;

public class CustomException extends Exception {
    private final String errCode;
    private final String errMsg;

    public CustomException(String errCode, String errMsg) {
        super(errMsg); // 부모 Exception 메시지 설정
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
