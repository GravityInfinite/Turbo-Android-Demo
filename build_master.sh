#!/bin/bash -l
source ~/.bash_profile
set -x

GRADLE_ROOT_FILE="./gradle.properties"

VERSION_NAME=$(grep "^VERSION" ${GRADLE_ROOT_FILE})

echo "in master branch, will not generate date timestamp"

./gradlew clean allPublish --stacktrace

echo "本次打包生成aar的版本号是：${VERSION_NAME}"
echo "本次打包生成aar的版本号是：${VERSION_NAME}"
echo "本次打包生成aar的版本号是：${VERSION_NAME}"
echo "本次打包生成aar的版本号是：${VERSION_NAME}"
