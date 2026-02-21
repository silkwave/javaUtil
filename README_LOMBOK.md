# Lombok 라이브러리 설치 방법

1. **Lombok JAR 추가**
   - `lib/` 폴더에 lombok JAR 파일을 다운로드합니다.
   - [Lombok 공식 사이트](https://projectlombok.org/download)에서 최신 JAR을 받아 `lib/lombok.jar`로 저장하세요.

2. **프로젝트 설정**
   - 이미 `.vscode/settings.json`의 `java.project.referencedLibraries`에 `lib/**/*.jar`가 포함되어 있으므로, 별도 설정 없이 인식됩니다.

3. **빌드/실행 시 classpath에 포함**
   - 컴파일: `javac -cp "lib/*" -d bin src/패키지/클래스.java`
   - 실행:   `java -cp "bin:lib/*" 패키지.클래스`

4. **IDE Lombok 플러그인 설치(권장)**
   - VS Code: "Lombok Annotations Support for VS Code" 확장 설치
   - IntelliJ: "Lombok" 플러그인 설치

---

**참고:**
- Lombok 어노테이션 사용 시, 반드시 JAR이 classpath에 있어야 하며, IDE 플러그인도 설치해야 자동 완성 및 오류 표시가 정상 동작합니다.
- 예시 어노테이션: `@Getter`, `@Setter`, `@Builder`, `@Data` 등

설치 후, Lombok 어노테이션이 정상 동작하는지 테스트 코드를 작성해보세요.