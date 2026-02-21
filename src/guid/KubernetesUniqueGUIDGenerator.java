package guid;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.lang.management.ManagementFactory;

public class KubernetesUniqueGUIDGenerator {

    // 영문 대문자와 숫자로 이루어진 문자열 집합 (랜덤 문자열 생성용)
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    // SecureRandom 객체를 사용하여 난수 생성
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * 고유한 GUID를 생성하는 메소드.
     * 이 GUID는 날짜, 컨테이너 이름, PID, 타임스탬프, 랜덤 문자열을 결합하여 생성됩니다.
     * 
     * @return 생성된 고유한 GUID
     */
    public static String generateUniqueGUID() {
        try {
            // 1. 현재 날짜를 "yyyyMMdd" 형식으로 가져옴
            String date = getCurrentDate();

            // 2. 환경변수로부터 컨테이너 이름을 가져옴. 없으면 기본값 "OSI01" 사용
            @SuppressWarnings("unused")
            String containerName = getContainerName();

            // 3. 현재 스레드의 PID를 가져옴
            String pid = printThreadPID();

            // 4. 현재 타임스탬프를 밀리초 단위로 가져오고, 그 중 뒤에서 7자리만 사용
            String lastDigits = getTimestampLastDigits();

            // 5. 8자리 랜덤 문자열 생성
            String randomString = generateRandomString(6);

            // 6. GUID 원본 생성 (날짜, PID, 컨테이너 이름, 타임스탬프, 랜덤 문자열 결합)
            // String rawGUID = String.format("%s:%s:%s:%s:%s", date, pid, containerName,
            // lastDigits, randomString).toUpperCase();
            String rawGUID = String.format("%s:%s:%s:%s", date, pid, lastDigits, randomString).toUpperCase();

            // 7. GUID의 길이가 32자리가 되도록 부족한 자리는 0으로 채워 반환
            String finalGUID = ensureGUIDLength(rawGUID);

            // 생성된 GUID 반환
            return finalGUID;

        } catch (Exception e) {
            // 예외 발생 시, GUID 생성 실패 예외를 던짐
            throw new RuntimeException("GUID 생성 실패", e);
        }
    }

    /**
     * 현재 날짜를 "yyyyMMdd" 형식으로 가져오는 메소드.
     * 
     * @return 현재 날짜 (yyyyMMdd 형식)
     */
    private static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDate.format(dateFormatter);
    }

    /**
     * 환경변수로부터 컨테이너 이름을 가져오는 메소드.
     * 환경변수가 설정되지 않았다면 기본값 "OSI01"을 반환.
     * 
     * @return 컨테이너 이름
     */
    private static String getContainerName() {
        String containerName = System.getenv("CONTAINER_NAME");
        return (containerName != null && !containerName.isEmpty()) ? containerName : "OSI01";
    }

    /**
     * 현재 타임스탬프의 뒤에서 7자리만 추출하는 메소드.
     * 
     * @return 타임스탬프 마지막 7자리
     */
    private static String getTimestampLastDigits() {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        return timestamp.length() >= 7 ? timestamp.substring(timestamp.length() - 7) : timestamp;
    }

    /**
     * 지정된 길이만큼 영문 대소문자와 숫자로 이루어진 랜덤 문자열을 생성합니다.
     * 
     * @param length 생성할 문자열의 길이
     * @return 랜덤으로 생성된 문자열
     */
    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);

        // 지정된 길이만큼 랜덤한 문자 선택
        for (int i = 0; i < length; i++) {
            // CHARACTERS에서 랜덤한 인덱스를 선택하여 문자를 얻음
            int index = secureRandom.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }

        return stringBuilder.toString();
    }

    /**
     * GUID의 길이가 32자리가 되도록 부족한 자리는 0으로 채우는 메소드.
     * 
     * @param rawGUID GUID 원본
     * @return 길이가 32인 GUID
     */
    private static String ensureGUIDLength(String rawGUID) {
        // GUID의 길이가 32자리가 되지 않으면, 오른쪽에 0을 채워 길이를 맞추고 반환
        return rawGUID.length() == 32 ? rawGUID : String.format("%-32s", rawGUID).replace(' ', '0');

        // return rawGUID;
    }

    /**
     * 현재 JVM 프로세스의 PID를 반환하는 메소드.
     * 
     * @return 현재 스레드의 PID
     */
    private static String printThreadPID() {
        // 현재 JVM 프로세스의 이름에서 PID를 추출
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();

        String pid = jvmName.split("@")[0]; // "pid@hostname" 형식에서 pid 부분 추출
        @SuppressWarnings("deprecation")
        long threadId = Thread.currentThread().getId(); // "pid@hostname" 형식에서 pid 부분 추출

        // System.out.println(jvmName + " GUID [" + pid + "] ");

        // System.out.println("Thread ID: " + Thread.currentThread().getId() + " is
        // executing.");
        // System.out.println("Thread Name: " + Thread.currentThread().getName() + " is
        // executing.");

        return pid + String.format("%02d", threadId);
    }

    /**
     * main 메소드 (테스트용)
     * GUID를 여러 번 생성하여 출력
     * 
     * @param args 실행 시 인자 (사용되지 않음)
     */
    public static void main(String[] args) {
        // 100번의 GUID를 생성하여 출력
        for (int i = 0; i < 100; i++) {
            System.out.println(i + " GUID [" + generateUniqueGUID() + "]\n");
        }
    }
}
