package bnbk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TYPE02 BOOKPR : 통장기장출력 메시지
 * str_chnnlsr.h  PRT_BNBK_MSG / PRT_BNBK_MSG1 변환
 *
 * C 원본:
 *   char FuncSect    [ 6]   FUNC 구분
 *   char SkipLineCnt [ 2]   Skip라인수
 *   char DataCnt     [ 3]   인자라인수
 *   char BnbkData[76][120]  인자내용 * 76 Line
 */
public class PrtBnbkMsg {

    public static final int MAX_LINES     = 76;   // BnbkData 행 수
    public static final int LINE_BYTES    = 120;  // BnbkData 열(1행 바이트)
    public static final int AUTO_TURN_AT  = 24;   // 자동 페이지 넘김 기준 라인

    // 헤더 필드
    private String funcSect    = "";   // [ 6] FUNC 구분
    private int    skipLineCnt = 0;    // [ 2] Skip라인수
    private int    dataCnt     = 0;    // [ 3] 인자라인수 (실제 기록 라인 수)

    // 인자내용 — BnbkData[76][120]
    // Java: List 로 관리 → build 시 고정 배열로 직렬화
    private final List<byte[]> bnbkLines = new ArrayList<>(MAX_LINES);

    // ---- getter / setter ----
    public String getFuncSect()    { return funcSect; }
    public int    getSkipLineCnt() { return skipLineCnt; }
    public int    getDataCnt()     { return dataCnt; }
    public List<byte[]> getBnbkLines() { return Collections.unmodifiableList(bnbkLines); }

    public void setFuncSect(String funcSect)       { this.funcSect    = funcSect; }
    public void setSkipLineCnt(int skipLineCnt)    { this.skipLineCnt = skipLineCnt; }

    /** 라인 추가 (MAX_LINES 초과 시 예외) */
    public void addLine(byte[] lineData) {
        if (bnbkLines.size() >= MAX_LINES)
            throw new IllegalStateException("BnbkData 최대 라인 초과: " + MAX_LINES);
        bnbkLines.add(Arrays.copyOf(lineData, LINE_BYTES));
        dataCnt = bnbkLines.size();
    }

    /**
     * C 구조체 직렬화
     * FuncSect(6) + SkipLineCnt(2) + DataCnt(3) + BnbkData[76][120]
     */
    public byte[] serialize(java.nio.charset.Charset charset) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        try {
            out.write(lpad(funcSect,    6, charset));
            out.write(lpad(String.valueOf(skipLineCnt), 2, charset));
            out.write(lpad(String.valueOf(dataCnt),     3, charset));
            for (byte[] line : bnbkLines) {
                out.write(Arrays.copyOf(line, LINE_BYTES));
            }
            // 나머지 빈 라인 공백 패딩
            byte[] emptyLine = new byte[LINE_BYTES];
            Arrays.fill(emptyLine, (byte) 0x20);
            for (int i = bnbkLines.size(); i < MAX_LINES; i++) {
                out.write(emptyLine);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    private static byte[] lpad(String s, int len, java.nio.charset.Charset cs) {
        byte[] raw  = s.getBytes(cs);
        byte[] dest = new byte[len];
        Arrays.fill(dest, (byte) 0x20);
        System.arraycopy(raw, 0, dest, 0, Math.min(raw.length, len));
        return dest;
    }
}
