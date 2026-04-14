package bnbk;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 장비 제어 프로토콜 — def_edtchnnl.h 변환
 */
public class PrinterProtocol {

    public static final Charset CHARSET_BINARY = StandardCharsets.ISO_8859_1;
    public static final Charset CHARSET_KOREAN = Charset.forName("EUC-KR");

    public enum Command {
        LF          ("\u00FE\u0000\u0001"),
        START_BNBK  ("\u00FF\u0000\u0001\u0000"),
        END_BNBK    ("\u00FF\u0000\u0001\u0004"),
        REPEAT      ("\u001E\u0033"),
        HALF_COMMA  ("\u001E\u00C8\u002C"),
        HALF_SPACE  ("\u001E\u00C8\u0020"),
        HALF_PERIOD ("\u001E\u00C8\u002E");

        final byte[] bytes;

        Command(String code) {
            this.bytes = code.getBytes(StandardCharsets.ISO_8859_1);
        }

        public byte[] getBytes() { return bytes.clone(); }
        public int    length()   { return bytes.length;  }
    }

    public static class PacketBuilder {

        private static final int AUTO_TURN_LINE = 24;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        /** 외부 공개 — 부호 확장 방지 */
        public void write(byte b)     { buffer.write(b & 0xFF); }
        /** 외부 공개 — raw 바이트 배열 직접 기록 (서비스 레이어용) */
        public void write(byte[] buf) { buffer.write(buf, 0, buf.length); }

        public PacketBuilder append(Command cmd) {
            buffer.write(cmd.bytes, 0, cmd.bytes.length);
            return this;
        }

        /** SP: REPEAT(2) + count(1) + 0x20(1) */
        public PacketBuilder appendSpace(int count) {
            validate(count);
            append(Command.REPEAT);
            write((byte) count);
            write((byte) 0x20);
            return this;
        }

        /** LF: LF_COMMAND(3) + count(1) */
        public PacketBuilder appendLineFeed(int count) {
            validate(count);
            append(Command.LF);
            write((byte) count);
            return this;
        }

        /** Auto turn: 24라인 페이지 넘김 */
        public PacketBuilder appendAutoTurn() {
            append(Command.LF);
            write((byte) AUTO_TURN_LINE);
            return this;
        }

        /** EUC-KR 텍스트 */
        public PacketBuilder appendText(String text) {
            if (text == null || text.isEmpty()) return this;
            write(text.getBytes(CHARSET_KOREAN));
            return this;
        }

        /** 고정 길이 필드: EUC-KR 후 maxBytes 맞춤 패딩/절삭 */
        public PacketBuilder appendField(String text, int maxBytes) {
            byte[] raw    = (text == null ? "" : text).getBytes(CHARSET_KOREAN);
            byte[] padded = new byte[maxBytes];
            Arrays.fill(padded, (byte) 0x20);
            System.arraycopy(raw, 0, padded, 0, Math.min(raw.length, maxBytes));
            write(padded);
            return this;
        }

        private void validate(int count) {
            if (count < 0 || count > 255)
                throw new IllegalArgumentException("count 범위 초과: " + count);
        }

        public byte[] build() { return buffer.toByteArray(); }
        public int    size()  { return buffer.size(); }

        public String toHexDump() {
            byte[] data = buffer.toByteArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                if (i % 16 == 0) sb.append(String.format("%04X  ", i));
                sb.append(String.format("%02X ", data[i] & 0xFF));
                if (i % 16 == 7)  sb.append(' ');
                if (i % 16 == 15) asciiBar(sb, data, i - 15, 16);
            }
            int rem = data.length % 16;
            if (rem != 0) {
                sb.append(" ".repeat((16 - rem) * 3 + (rem <= 8 ? 1 : 0)));
                asciiBar(sb, data, data.length - rem, rem);
            }
            return sb.toString();
        }

        public String toHexString() {
            byte[] data = buffer.toByteArray();
            StringBuilder sb = new StringBuilder(data.length * 3);
            for (byte b : data) sb.append(String.format("%02X ", b));
            return sb.toString().trim();
        }

        private static void asciiBar(StringBuilder sb, byte[] d, int from, int len) {
            sb.append(" |");
            for (int i = from; i < from + len; i++) {
                int b = d[i] & 0xFF;
                sb.append((b >= 0x20 && b < 0x7F) ? (char) b : '.');
            }
            sb.append("|\n");
        }
    }
}