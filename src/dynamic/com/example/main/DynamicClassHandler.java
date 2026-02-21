package dynamic.com.example.main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;  
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicClassHandler {

    private String currentJarFileName = ""; // 현재 로드된 JAR 파일 이름
    private String jarFilePath = ""; // 현재 로드된 JAR 파일 경로
    private Map<String, String> latestJarFilePaths = new HashMap<>(); // 최신 JAR 파일 경로 맵
    private ClassLoader classLoader; // 클래스 로더

    private static final String jarFileDirectory = "/home/silkwave/apps/java/javaUtil/jar/"; // JAR 파일이 위치한 디렉토리

    // 동적으로 클래스를 처리하는 메서드
    public Map<String, String> handleDynamicClass(Map<String, Object> params) throws IOException, URISyntaxException {
        String className = (String) params.get("className"); // 클래스 이름
        String methodName = (String) params.get("methodName"); // 메서드 이름
        @SuppressWarnings("unchecked")
        Map<String, String> input = (Map<String, String>) params.get("input"); // 입력 값
        String jarFileName = (String) params.get("jarFileName"); // JAR 파일 이름

        updateJarFilePath(jarFileName , className); // JAR 파일 경로 업데이트
        // loadDynamicClass(className); // 동적으로 클래스를 로드하는 코드 (주석 처리됨)

        if (isClassLoaded(className)) { // 클래스가 로드되었는지 확인
            Object instance = createInstance(className, input); // 클래스 인스턴스를 생성
            return invokeMethod(instance, methodName); // 메서드 호출
        } else {
            System.out.println("Class " + className + " is not loaded.");
        }
        return null;
    }

    // JAR 파일 경로를 업데이트하고 최신 JAR 파일을 로드하는 메서드
    private void updateJarFilePath(String jarFileName , String className) throws IOException, URISyntaxException {
        List<File> fileList = new ArrayList<>();
        Pattern pattern = Pattern.compile( jarFileName + "_(\\d+)\\.jar$"); // JAR 파일 이름 패턴

        try {
            Files.walkFileTree(Paths.get(jarFileDirectory), new SimpleFileVisitor<Path>() { // 디렉토리 트리를 순회하며 파일 찾기
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Matcher matcher = pattern.matcher(file.getFileName().toString());
                    if (matcher.matches()) {
                        fileList.add(file.toFile()); // 파일 목록에 추가
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileList.sort(Comparator.comparing(File::getName)); // 파일 이름으로 정렬

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 각 파일의 생성 시간 출력
        for (File file : fileList) {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            LocalDateTime creationTime = LocalDateTime.ofInstant(attrs.creationTime().toInstant(),
                ZoneId.systemDefault());
            System.out.println("파일: " + file.getName() + " 생성 시간: " + formatter.format(creationTime));
        }

        // 최신 JAR 파일을 찾고 로드
        if (!fileList.isEmpty()) {
            File latestFile = fileList.get(fileList.size() - 1);
            String latestJarFilePath = latestFile.getAbsolutePath();
            String latestJarFileName = latestFile.getName();

            // 현재 로드된 JAR 파일과 최신 JAR 파일이 다르면 업데이트
            if (!latestJarFileName.equals(currentJarFileName)) {
                jarFilePath = latestJarFilePath;
                currentJarFileName = latestJarFileName;
                latestJarFilePaths.put(currentJarFileName, jarFilePath); // 최신 JAR 파일 경로 맵 업데이트

                System.out.println("\n\n\n\n***********************************");
                System.out.println("최신 JAR 파일 경로: " + jarFilePath);
                System.out.println("***********************************\n\n\n\n");
                loadDynamicClass(className); // 최신 JAR 파일에서 클래스 로드
            }
        } else {
            System.out.println("mod로 시작하는 JAR 파일을 찾을 수 없습니다.");
        }

    }

    // 동적으로 클래스를 로드하는 메서드
    private void loadDynamicClass(String className) throws IOException, URISyntaxException {
        if (classLoader != null) {
            try {
                ((URLClassLoader) classLoader).close(); // 이전 클래스 로더를 닫음
                classLoader = null;
                System.out.println("Closing previous class loader...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            URI uri = Paths.get(jarFilePath).toUri(); // JAR 파일 경로를 URI로 변환
            classLoader = new URLClassLoader(new URL[] { uri.toURL() }); // 새로운 클래스 로더 생성
            System.out.println("Loading class: " + className + " from JAR file: " + jarFilePath);

            Class<?> clazz = classLoader.loadClass(className); // 클래스를 로드
            System.out.println("Loaded class: " + clazz.getName());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 클래스가 로드되었는지 확인하는 메서드
    private boolean isClassLoaded(String className) {
        try {
            Class.forName(className, false, classLoader); // 클래스 로딩 시도
            return true;
        } catch (ClassNotFoundException e) {
            return false; // 클래스가 로드되지 않으면 false 반환
        }
    }

    // 클래스 인스턴스를 생성하는 메서드
    private Object createInstance(String className, Map<String, String> input) {
        try {
            Class<?> clazz = classLoader.loadClass(className); // 클래스를 로드
            Constructor<?> constructor = clazz.getDeclaredConstructor(Map.class); // 생성자 찾기
            constructor.setAccessible(true); // 접근 가능하도록 설정
            return constructor.newInstance(input); // 인스턴스 생성
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 메서드를 호출하는 메서드
    @SuppressWarnings("unchecked")
    private Map<String, String> invokeMethod(Object instance, String methodName) {
        try {
            Method method = instance.getClass().getMethod(methodName); // 메서드 찾기
            method.setAccessible(true); // 접근 가능하도록 설정
            Object result = method.invoke(instance); // 메서드 호출
            if (result instanceof Map) {
                return (Map<String, String>) result; // 결과가 Map인 경우 반환
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
