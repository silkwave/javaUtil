#!/bin/bash

# Java 파일 컴파일
echo "Compiling Java files..."
javac -d ../../out ./com/example/mod/*.java

# 추가 모듈 JAR 파일 생성
jar_file_name="../../jar/mod_$(date "+%Y%m%d%H%M%S").jar"
jar cvf "$jar_file_name" -C ../../out dynamic/com/example/mod .

# JAR 파일 디렉터리 경로
jar_directory="../../jar"

# 최신 10개의 JAR 파일을 제외한 나머지 파일 삭제
echo "Deleting old JAR files, keeping only the latest 10..."
cd "$jar_directory"

# 파일을 날짜순으로 정렬하고 가장 오래된 파일을 삭제
ls -1t mod_*.jar | sed -e '1,10d' | xargs rm -f
echo "Old JAR files deleted. Only the latest 10 are kept."

