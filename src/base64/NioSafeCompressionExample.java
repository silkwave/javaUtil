package base64;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.zip.*;

public class NioSafeCompressionExample {

    private static final int BUFFER_SIZE = 256 * 1024; // 256KB

    public static void main(String[] args) {
        String srcFile = "logfile.txt";
        String gzFile = "logfile.gz";
        String encFile = "logfile.base64";
        String decFile = "logfile_decoded.gz";
        String outFile = "logfile_uncompressed.txt";

        measureTime(() -> gzip(srcFile, gzFile), "GZIP 압축");
        measureTime(() -> base64Encode(gzFile, encFile), "Base64 인코딩");
        measureTime(() -> base64Decode(encFile, decFile), "Base64 디코딩");
        measureTime(() -> ungzip(decFile, outFile), "GZIP 압축 해제");
    }

    private static void measureTime(Runnable task, String taskName) {
        long start = System.nanoTime();
        task.run();
        long end = System.nanoTime();
        System.out.println(taskName + " 실행 시간: " + ((end - start) / 1_000_000) + "ms");
    }

    // ------------------- GZIP -------------------
    private static void gzip(String srcFile, String outFile) {
        Path srcPath = Paths.get(srcFile);
        Path outPath = Paths.get(outFile);
        try (FileChannel in = FileChannel.open(srcPath, StandardOpenOption.READ);
             GZIPOutputStream gzipOut = new GZIPOutputStream(Files.newOutputStream(outPath));
             WritableByteChannel out = Channels.newChannel(gzipOut)) {

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            while (in.read(buffer) != -1) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException("GZIP 압축 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private static void ungzip(String srcFile, String outFile) {
        Path srcPath = Paths.get(srcFile);
        Path outPath = Paths.get(outFile);
        try (FileChannel out = FileChannel.open(outPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             GZIPInputStream gzipIn = new GZIPInputStream(Files.newInputStream(srcPath))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gzipIn.read(buffer)) != -1) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, 0, bytesRead);
                out.write(bb);
            }
        } catch (IOException e) {
            throw new RuntimeException("GZIP 압축 해제 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // ------------------- Base64 -------------------
    private static void base64Encode(String srcFile, String outFile) {
        Base64.Encoder encoder = Base64.getEncoder();
        try (InputStream fis = Files.newInputStream(Paths.get(srcFile));
             OutputStream fos = Files.newOutputStream(Paths.get(outFile));
             OutputStream base64Out = encoder.wrap(fos)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                base64Out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException("Base64 인코딩 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private static void base64Decode(String srcFile, String outFile) {
        Base64.Decoder decoder = Base64.getDecoder();
        try (InputStream fis = Files.newInputStream(Paths.get(srcFile));
             InputStream base64In = decoder.wrap(fis);
             OutputStream fos = Files.newOutputStream(Paths.get(outFile))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = base64In.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException("Base64 디코딩 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
