package consts;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 통장 채널 편집 상수 정의
 * def_edtchnnl.h 변환 (String 리팩토링)
 *
 * ※ 바이너리 커맨드를 String으로 보존하려면 반드시 CHARSET(ISO-8859-1) 사용
 *    다른 인코딩으로 getBytes() 호출 시 0xFF 등 high-byte 손상 위험
 *
 * C 원본 매크로:
 *   SP(dst, cnt) { memcpy(dst, REPEAT_COMMAND, 2); dst+=2; *dst=(char)cnt; dst+=1; *dst=0x20; dst+=1; }
 *   LF(dst, cnt) { memcpy(dst, LF_COMMAND, 3);     dst+=3; *dst=(char)cnt; dst+=1; }
 */
public final class EdtChnnlDef {

    private EdtChnnlDef() {}

    /**
     * 바이너리 보존용 고정 Charset
     * ISO-8859-1: 0x00~0xFF 를 1:1 매핑 → high-byte 손상 없음
     * ※ toBytes() 호출 시 반드시 이 charset만 사용할 것
     */
    public static final Charset CHARSET = StandardCharsets.ISO_8859_1;

    // -------------------------------------------------------------------------
    // 최대 항목 수
    // -------------------------------------------------------------------------
    public static final int MAX_ITEMS = 128;

    // -------------------------------------------------------------------------
    // 제어 커맨드 String (ISO-8859-1 기준 바이너리 보존)
    // Java 유니코드 이스케이프 — Latin-1 1:1 대응
    // -------------------------------------------------------------------------

    /** 개행 Command          : \xFE\x00\x01          (3 bytes) */
    public static final String LF_COMMAND         = "\u00FE\u0000\u0001";

    /** 통장기장 시작 Command  : \xFF\x00\x01\x00      (4 bytes) */
    public static final String START_BNBK_COMMAND = "\u00FF\u0000\u0001\u0000";

    /** 통장기장 종료 Command  : \xFF\x00\x01\x04      (4 bytes) */
    public static final String END_BNBK_COMMAND   = "\u00FF\u0000\u0001\u0004";

    /** Half Pitch Comma      : \x1E\xC8\x2C          (3 bytes) */
    public static final String HALF_PITCH_COMMA   = "\u001E\u00C8\u002C";

    /** Half Pitch Space      : \x1E\xC8\x20          (3 bytes) */
    public static final String HALF_PITCH_SPACE   = "\u001E\u00C8\u0020";

    /** Half Pitch Period     : \x1E\xC8\x2E          (3 bytes) */
    public static final String HALF_PITCH_PERIOD  = "\u001E\u00C8\u002E";

    /** Repeat Command        : \x1E\x33              (2 bytes) */
    public static final String REPEAT_COMMAND     = "\u001E\u0033";

    // -------------------------------------------------------------------------
    // 커맨드 길이 상수
    // -------------------------------------------------------------------------
    public static final int LF_COMMAND_LEN         = 3;
    public static final int START_BNBK_COMMAND_LEN = 4;
    public static final int END_BNBK_COMMAND_LEN   = 4;
    public static final int HALF_PITCH_COMMA_LEN   = 3;
    public static final int HALF_PITCH_SPACE_LEN   = 3;
    public static final int HALF_PITCH_PERIOD_LEN  = 3;
    public static final int REPEAT_COMMAND_LEN     = 2;

    // -------------------------------------------------------------------------
    // 커맨드 Writer  (C 매크로 → Java 메서드 / dst 포인터 → StringBuilder)
    // -------------------------------------------------------------------------

    /**
     * SPACE Command 추가
     * REPEAT_COMMAND(2) + cnt(1) + 0x20(1) = 4 chars
     *
     * @param sb  출력 버퍼
     * @param cnt 반복 횟수
     */
    public static void writeSP(StringBuilder sb, int cnt) {
        sb.append(REPEAT_COMMAND);   // \x1E\x33
        sb.append((char) cnt);       // 반복 횟수
        sb.append('\u0020');         // SPACE (0x20)
    }

    /**
     * 개행(LF) Command 추가
     * LF_COMMAND(3) + cnt(1) = 4 chars
     *
     * @param sb  출력 버퍼
     * @param cnt 줄 수
     */
    public static void writeLF(StringBuilder sb, int cnt) {
        sb.append(LF_COMMAND);       // \xFE\x00\x01
        sb.append((char) cnt);       // 줄 수
    }

    // -------------------------------------------------------------------------
    // 변환 유틸
    // -------------------------------------------------------------------------

    /**
     * StringBuilder → byte[]  (ISO-8859-1 고정)
     * OutputStream 전송 직전에만 호출할 것
     */
    public static byte[] toBytes(StringBuilder sb) {
        return sb.toString().getBytes(CHARSET);
    }

    /**
     * StringBuilder 내용을 OutputStream에 직접 기록
     */
    public static void flush(StringBuilder sb, OutputStream out) throws IOException {
        out.write(toBytes(sb));
    }

    // -------------------------------------------------------------------------
    // main — 사용 예제
    // -------------------------------------------------------------------------
    public static void main(String[] args) throws Exception {

        StringBuilder sb = new StringBuilder();

        // ── 통장기장 시작 ──────────────────────────────────────────
        sb.append(START_BNBK_COMMAND);

        // ── 개행 2줄 ──────────────────────────────────────────────
        writeLF(sb, 2);

        // ── 공백 5칸 ──────────────────────────────────────────────
        writeSP(sb, 5);

        // ── Half Pitch 구두점 샘플 ────────────────────────────────
        sb.append(HALF_PITCH_COMMA);
        sb.append(HALF_PITCH_SPACE);
        sb.append(HALF_PITCH_PERIOD);

        // ── 개행 1줄 ──────────────────────────────────────────────
        writeLF(sb, 1);

        // ── 통장기장 종료 ──────────────────────────────────────────
        sb.append(END_BNBK_COMMAND);

        // ── ISO-8859-1 변환 후 덤프 ───────────────────────────────
        byte[] result = toBytes(sb);
        System.out.printf("총 %d bytes%n", result.length);
        System.out.println("─".repeat(56));

        // 16진수 + ASCII 혼합 덤프 (16바이트 1행)
        for (int i = 0; i < result.length; i++) {
            if (i % 16 == 0) System.out.printf("%04X  ", i);
            System.out.printf("%02X ", result[i] & 0xFF);
            if (i % 16 == 7)  System.out.print(" ");
            if (i % 16 == 15) printAscii(result, i - 15, 16);
        }
        // 마지막 행 나머지 정렬
        int rem = result.length % 16;
        if (rem != 0) {
            int pad = (16 - rem) * 3 + (rem <= 8 ? 1 : 0);
            System.out.print(" ".repeat(pad));
            printAscii(result, result.length - rem, rem);
        }

        // ── 바이트 무결성 검증 ─────────────────────────────────────
        System.out.println();
        verifyCommands();
    }

    /** 각 커맨드 String → byte[] 변환 후 기대값과 비교 검증 */
    private static void verifyCommands() {
        System.out.println("[ 커맨드 바이트 검증 ]");
        verify("LF_COMMAND",         LF_COMMAND,         0xFE, 0x00, 0x01);
        verify("START_BNBK_COMMAND", START_BNBK_COMMAND, 0xFF, 0x00, 0x01, 0x00);
        verify("END_BNBK_COMMAND",   END_BNBK_COMMAND,   0xFF, 0x00, 0x01, 0x04);
        verify("HALF_PITCH_COMMA",   HALF_PITCH_COMMA,   0x1E, 0xC8, 0x2C);
        verify("HALF_PITCH_SPACE",   HALF_PITCH_SPACE,   0x1E, 0xC8, 0x20);
        verify("HALF_PITCH_PERIOD",  HALF_PITCH_PERIOD,  0x1E, 0xC8, 0x2E);
        verify("REPEAT_COMMAND",     REPEAT_COMMAND,     0x1E, 0x33);
    }

    private static void verify(String name, String cmd, int... expected) {
        byte[] actual = cmd.getBytes(CHARSET);
        boolean ok = actual.length == expected.length;
        if (ok) {
            for (int i = 0; i < actual.length; i++) {
                if ((actual[i] & 0xFF) != expected[i]) { ok = false; break; }
            }
        }
        System.out.printf("  %-24s %s%n", name, ok ? "OK" : "FAIL !!!");
    }

    /** 지정 범위를 ASCII로 출력 (제어문자는 '.'으로 대체) */
    private static void printAscii(byte[] data, int from, int len) {
        System.out.print(" |");
        for (int i = from; i < from + len; i++) {
            int b = data[i] & 0xFF;
            System.out.print((b >= 0x20 && b < 0x7F) ? (char) b : '.');
        }
        System.out.println("|");
    }
}