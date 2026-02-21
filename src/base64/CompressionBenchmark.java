package base64;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionBenchmark {

    private static final int BUFFER_SIZE = 8192;
    private static final File TEMP_DIR = new File("benchmark_temp");

    public static void main(String[] args) throws Exception {
        if (!TEMP_DIR.exists())
            TEMP_DIR.mkdirs();

        int[] sizesMB = { 10, 100, 500 }; // 테스트할 파일 크기(MB)
        for (int size : sizesMB) {
            File inputFile = new File(TEMP_DIR, "sample_" + size + "MB.txt");
            createSampleFile(inputFile, size);

            System.out.println("\n==============================");
            System.out.printf("📁 테스트 파일: %s (%d MB)%n", inputFile.getName(), size);

            // IO 방식 압축 + 해제
            File ioCompressed = new File(TEMP_DIR, "sample_" + size + "_io.gz");
            File ioDecompressed = new File(TEMP_DIR, "sample_" + size + "_io_un.txt");

            long ioCompressTime = measure(() -> {
                try {
                    compressIO(inputFile, ioCompressed);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            long ioDecompressTime = measure(() -> {
                try {
                    decompressIO(ioCompressed, ioDecompressed);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            printResult("IO", inputFile, ioCompressed, ioDecompressed, ioCompressTime, ioDecompressTime);

            // NIO 방식 압축 + 해제
            File nioCompressed = new File(TEMP_DIR, "sample_" + size + "_nio.gz");
            File nioDecompressed = new File(TEMP_DIR, "sample_" + size + "_nio_un.txt");

            long nioCompressTime = measure(() -> {
                try {
                    compressNIO(inputFile, nioCompressed);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            long nioDecompressTime = measure(() -> {
                try {
                    decompressNIO(nioCompressed, nioDecompressed);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            printResult("NIO", inputFile, nioCompressed, nioDecompressed, nioCompressTime, nioDecompressTime);
        }
    }

    // ------------------- 압축 / 해제 ---------------------
    public static void compressIO(File input, File output) throws IOException {
        try (FileInputStream fis = new FileInputStream(input);
             FileOutputStream fos = new FileOutputStream(output);
             GZIPOutputStream gzip = new GZIPOutputStream(fos)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) != -1)
                gzip.write(buffer, 0, len);
        }
    }

    public static void decompressIO(File input, File output) throws IOException {
        try (FileInputStream fis = new FileInputStream(input);
             GZIPInputStream gzip = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(output)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gzip.read(buffer)) != -1)
                fos.write(buffer, 0, len);
        }
    }

    public static void compressNIO(File input, File output) throws IOException {
        try (FileInputStream fis = new FileInputStream(input);
             FileChannel in = fis.getChannel();
             FileOutputStream fos = new FileOutputStream(output);
             GZIPOutputStream gzip = new GZIPOutputStream(fos);
             WritableByteChannel out = Channels.newChannel(gzip)) {

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            while (in.read(buffer) != -1) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        }
    }

    public static void decompressNIO(File input, File output) throws IOException {
        try (FileInputStream fis = new FileInputStream(input);
             GZIPInputStream gzip = new GZIPInputStream(fis);
             ReadableByteChannel in = Channels.newChannel(gzip);
             FileOutputStream fos = new FileOutputStream(output);
             FileChannel out = fos.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            while (in.read(buffer) != -1) {
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        }
    }

    // ------------------- 도우미 ---------------------
    private static void createSampleFile(File file, int sizeMB) throws IOException {
        if (file.exists())
            return;
        System.out.printf("🧱 샘플 파일 생성 중... (%d MB)%n", sizeMB);
        Random rand = new Random();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < sizeMB * 1024 * 1024 / 100; i++)
                writer.write("DATA_" + rand.nextInt(9999) + "_RANDOM_TEXT_SAMPLE\n");
        }
    }

    private static long measure(Runnable task) {
        long start = System.nanoTime();
        task.run();
        long end = System.nanoTime();
        return (end - start) / 1_000_000;
    }

    private static void printResult(String label, File input, File compressed, File decompressed,
                                    long compressTime, long decompressTime) {
        long inSize = input.length();
        long compSize = compressed.length();
        long outSize = decompressed.length();
        double ratio = (double) compSize / inSize * 100;
        boolean sameSize = inSize == outSize;

        System.out.printf("🚀 %s 방식: 압축 %6d ms | 해제 %6d ms | 압축률 %.2f%% | 복원 정상? %s%n",
                label, compressTime, decompressTime, ratio, sameSize ? "✅" : "❌");
    }
}
