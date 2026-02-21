# ==================================================
# 언팩(Unpack)이 필요한 라이브러리 목록
# - 파일명 + 경로 기준
# - 동일한 이름의 JAR 허용
# ==================================================

include=../ODSI01/libs/security-core.jar
include=../ODSI01/libs/org1/auth-module.jar
include=../ODSI01/libs/org2/auth-module.jar


/**
 * ==================================================
 * 기관 제공 라이브러리 설정
 * ==================================================
 *
 * - 기관 라이브러리는 보안 정책(자기무결성 체크)이 적용됨
 * - 일부 라이브러리는 fat JAR 내부에서
 *   압축 해제(Unpack)된 상태로 존재해야 정상 동작
 */

/* --------------------------------------------------
 * 컴파일 시점 참조용 라이브러리
 * -------------------------------------------------- */
compileOnly fileTree(dir: 'libs', include: ['**/*.jar'])

/* --------------------------------------------------
 * 실행 시 실제 사용되는 기관 제공 라이브러리
 * -------------------------------------------------- */
implementation fileTree(dir: '../ODSI01/libs', include: ['**/*.jar'])


tasks.bootJar {
    enabled = true

    /* --------------------------------------------------
     * 언팩 대상 라이브러리 설정 파일
     * -------------------------------------------------- */
    def rulesFile = file("../ODSI01/libs/unpack-rules.conf")

    /* --------------------------------------------------
     * 언팩 대상 JAR 목록 생성
     *
     * 1. include= 로 시작하는 라인만 필터링
     * 2. File 객체로 변환
     * 3. canonicalPath 로 경로 정규화
     * 4. 실제 존재하는 파일만 사용
     * -------------------------------------------------- */
    def unpackTargetFiles = rulesFile.readLines()
        .findAll { line ->
            line?.trim() &&
            !line.startsWith("#") &&
            line.startsWith("include=")
        }
        .collect { line ->
            file(line.substring("include=".length()).trim())
        }
        .collect { it.canonicalFile }
        .findAll { target ->
            if (!target.exists()) {
                logger.warn(
                    "[bootJar][WARN] unpack 대상 파일이 존재하지 않습니다: ${target.path}"
                )
                return false
            }
            true
        }

    /* --------------------------------------------------
     * 언팩 대상 판단 로직
     *
     * - 파일명 기준 ❌
     * - canonicalPath 기준 ⭕
     *
     * → 동일 이름 JAR이 있어도 정확히 구분됨
     * -------------------------------------------------- */
    requiresUnpack { jarFile ->
        unpackTargetFiles.any { target ->
            jarFile.canonicalPath == target.canonicalPath
        }
    }
}
