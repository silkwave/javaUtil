// Main.java
package CustomException;

public class Main {
    public static void main(String[] args) {
        try {
            throw new CustomException("E001", "잘못된 요청입니다.");
        } catch (CustomException e) {
            String errorMessage = OpenBankErrProc.of(e, "OPENBANKING")
                    .createOpenBankErrMessage(e.getErrCode(), e.getErrMsg());

            System.out.println("최종 에러 메시지:\n" + errorMessage);
        }
    }
}
