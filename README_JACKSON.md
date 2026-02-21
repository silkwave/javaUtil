# Jackson Annotations 설치 방법

1. **JAR 파일 다운로드**
   - [Maven Central](https://search.maven.org/artifact/com.fasterxml.jackson.core/jackson-annotations)에서 최신 jackson-annotations JAR 파일을 다운로드합니다.
   - 예시: `jackson-annotations-2.17.1.jar` (버전은 최신으로 선택)
   - 다운로드한 파일을 `lib/` 폴더에 복사합니다.

2. **프로젝트 설정**
   - `.vscode/settings.json`의 `java.project.referencedLibraries`에 이미 `lib/**/*.jar`가 포함되어 있으므로 별도 추가 설정 없이 인식됩니다.

3. **컴파일/실행 시 classpath에 포함**
   - 컴파일: `javac -cp "lib/*" -d bin src/패키지/클래스.java`
   - 실행:   `java -cp "bin:lib/*" 패키지.클래스`

---

**참고:**
- Jackson 어노테이션(`@JsonProperty`, `@JsonIgnore` 등)을 사용하려면 `jackson-annotations` JAR만 있으면 됩니다.
- 직렬화/역직렬화까지 하려면 `jackson-core`, `jackson-databind`도 함께 사용하는 것이 일반적입니다.
- 필요하다면 추가 JAR도 `lib/`에 넣고 classpath에 포함하세요.

설치 후, 어노테이션이 정상 동작하는지 테스트 코드를 작성해보세요.