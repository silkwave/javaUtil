package consts;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 장비 제어 프로토콜 (최종)
 *
 * ✔ 내부: byte[] 기반 (바이너리 안전)
 * ✔ 외부: String 지원 (ISO-8859-1)
 * ✔ 장비 전송까지 포함
 */
public final class PrinterProtocol {

    private PrinterProtocol() {}

    // =========================
    // 1. Charset 정의
    // =========================

    /** 한글 출력용 */
    private static final Charset KOREAN_CHARSET = Charset.forName("EUC-KR");

    /** 바이너리 안전 String 변환용 */
    private static final Charset BINARY_CHARSET = StandardCharsets.ISO_8859_1;

    // =========================
    // 2. Command 정의
    // =========================
    public enum Command {

        LF              (0xFE, 0x00, 0x01),
        START_BNBK      (0xFF, 0x00, 0x01, 0x00),
        END_BNBK        (0xFF, 0x00, 0x01, 0x04),
        REPEAT          (0x1E, 0x33),

        HALF_COMMA      (0x1E, 0xC8, 0x2C),
        HALF_SPACE      (0x1E, 0xC8, 0x20),
        HALF_PERIOD     (0x1E, 0xC8, 0x2E);

        private final byte[] bytes;

        Command(int... values) {
            this.bytes = new byte[values.length];
            for (int i = 0; i < values.length; i++) {
                this.bytes[i] = (byte) values[i];
            }
        }

        byte[] raw() {
            return bytes;
        }
    }

    // =========================
    // 3. Packet 생성 (C 스타일)
    // =========================

    private static void write(ByteArrayOutputStream out, byte b) {
        out.write(b & 0xFF);
    }

    private static void write(ByteArrayOutputStream out, byte[] bytes) {
        out.write(bytes, 0, bytes.length);
    }

    private static void lf(ByteArrayOutputStream out, int count) {
        validate(count);
        write(out, Command.LF.raw());
        write(out, (byte) count);
    }

    private static void sp(ByteArrayOutputStream out, int count) {
        validate(count);
        write(out, Command.REPEAT.raw());
        write(out, (byte) count);
        write(out, (byte) 0x20);
    }

    /**
     * byte[] 패킷 생성
     */
    public static byte[] createBnBk(String text) {

        ByteArrayOutputStream out = new ByteArrayOutputStream(128);

        write(out, Command.START_BNBK.raw());
        lf(out, 2);
        sp(out, 5);

        if (text != null && !text.isEmpty()) {
            write(out, text.getBytes(KOREAN_CHARSET));
        }

        write(out, Command.HALF_COMMA.raw());
        lf(out, 1);
        write(out, Command.END_BNBK.raw());

        return out.toByteArray();
    }

    // =========================
    // 4. byte[] ↔ String 변환
    // =========================

    /**
     * byte[] → String (바이너리 안전)
     */
    public static String toBinaryString(byte[] packet) {
        return new String(packet, BINARY_CHARSET);
    }

    /**
     * String → byte[]
     */
    public static byte[] fromBinaryString(String packet) {
        return packet.getBytes(BINARY_CHARSET);
    }

    // =========================
    // 5. 장비 전송
    // =========================

    /**
     * String 기반 전송 (내부에서 byte 변환)
     */
    public static void send(String packetStr, OutputStream out) throws Exception {
        out.write(fromBinaryString(packetStr));
        out.flush();
    }

    /**
     * Socket 직접 전송
     */
    public static void send(String packetStr, Socket socket) throws Exception {
        send(packetStr, socket.getOutputStream());
    }

    // =========================
    // 6. HEX 디버그
    // =========================
    public static String hex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 3);
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    // =========================
    // 7. 테스트
    // =========================
    public static void main(String[] args) throws Exception {

        // 1. byte[] 생성
        byte[] packet = createBnBk("기장테스트");

        // 2. String 변환 (외부 시스템용)
        String packetStr = toBinaryString(packet);

        // 3. 다시 byte[] (전송용)
        byte[] send = fromBinaryString(packetStr);

        // HEX 확인
        System.out.println("[HEX]");
        System.out.println(hex(send));
        System.out.println("LEN: " + send.length);

        // -------------------------
        // 실제 전송 예시
        // -------------------------
        /*
        Socket socket = new Socket("127.0.0.1", 9999);
        send(packetStr, socket);
        socket.close();
        */
    }

    // =========================
    // validate
    // =========================
    private static void validate(int count) {
        if (count < 0 || count > 255) {
            throw new IllegalArgumentException("count must be 0~255: " + count);
        }
    }
}