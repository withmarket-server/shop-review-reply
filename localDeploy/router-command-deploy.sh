# 루트 폴더로 이동
cd ..

# 루트 폴더를 path variable로 저장
ROOT_FOLDER=$(pwd)

# Kafka-consumer 폴더로 이동
cd $ROOT_FOLDER/adapter/router-command

chmod 755 ./gradlew

./gradlew build

cd build/libs

java -jar router-command-1.0.0.jar