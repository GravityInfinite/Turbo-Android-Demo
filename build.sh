#!/bin/bash -l
source ~/.bash_profile
set -x

GRADLE_ROOT_FILE="./gradle.properties"

VERSION_NAME=$(grep "^VERSION" ${GRADLE_ROOT_FILE})

CURRENT_GIT_BRANCH=$(git symbolic-ref --short -q HEAD)
if [ -z "$CURRENT_GIT_BRANCH"]; then
  CURRENT_GIT_BRANCH=${GIT_LOCAL_BRANCH}
else
  echo "git branch not null"
fi

random=$(date '+%m-%d-%H-%M-%S')

NEW_VERSION_NAME=$(echo ${VERSION_NAME}-${CURRENT_GIT_BRANCH}-${random})

sed -i -e "s/${VERSION_NAME}/${NEW_VERSION_NAME}/" ${GRADLE_ROOT_FILE}
VERSION_NAME=$(grep "^VERSION" ${GRADLE_ROOT_FILE})
echo "changed VERSION_NAME ${VERSION_NAME}"

./gradlew clean allPublish --stacktrace

echo "本次打包生成aar的版本号是：${VERSION_NAME}"
echo "本次打包生成aar的版本号是：${VERSION_NAME}"
echo "本次打包生成aar的版本号是：${VERSION_NAME}"
echo "本次打包生成aar的版本号是：${VERSION_NAME}"
