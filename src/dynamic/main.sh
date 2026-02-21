#!/bin/bash

# Java 파일 컴파일
echo "Compiling Java files..."
javac -d ../../out ./com/example/main/*.java

# JAR 파일 생성 (MANIFEST.MF 포함)
jar cvfm ../../jar/app.jar ./MANIFEST.MF -C  ../../out dynamic/com/example/main .

# JAR 파일 실행
echo "Running the JAR file..."
java -jar ../../jar/app.jar
