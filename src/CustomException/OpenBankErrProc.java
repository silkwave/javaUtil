// OpenBankErrProc.java
package CustomException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class OpenBankErrProc {
    private static final Logger logger = Logger.getLogger(OpenBankErrProc.class.getName());

    private final Exception exception;
    private final String systemCode;

    private OpenBankErrProc(Exception exception, String systemCode) {
        this.exception = exception;
        this.systemCode = systemCode;
    }

    public static OpenBankErrProc of(Exception e, String systemCode) {
        logger.info(">> 실행: OpenBankErrProc.of()");
        return new OpenBankErrProc(e, systemCode);
    }

    public String createOpenBankErrMessage(String code, String msg) {
        logger.info(">> 실행: OpenBankErrProc.createOpenBankErrMessage()");
        logger.warning("예외 클래스: " + exception.getClass().getSimpleName());

        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        logger.severe("스택 트레이스:\n" + stackTrace);

        return String.format("[%s] 에러코드: %s, 메시지: %s\n%s", systemCode, code, msg, stackTrace);
    }
}
