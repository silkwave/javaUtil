package subpub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
    // SLF4J Logger 인스턴스 생성
    private static final Logger logger = LoggerFactory.getLogger(HelloWorld.class);

    public static void main(String[] args) {
        // 로그 메시지 출력
        logger.info("Hello, World!");
    }
}
