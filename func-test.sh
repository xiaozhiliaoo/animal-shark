#!/bin/bash

scriptPath=$(cd $(dirname $0);pwd)
executingPath=$(pwd)

echo "cd ${scriptPath}"
cd ${scriptPath}

test_type="func"
platform=""
input=""
output=""
email=""

echo "欢迎使用shark-test工具，请按照以下引导填写测试选项"


while [[ "$platform" != "shark" && "$platform"!="all" ]]; do
    read -p "测试平台选择(shark)(默认:shark):" platform
    if [ -z "$platform" ]; then
        platform="shark"
    fi
done


while [[ -z "$input" ]]; do
    read -p "测试用例文件路径(默认test.json):" input
    if [ -z "$input" ]; then
        input="test.json"
    fi
done

read -p "报告输出路径(默认当前目录):" output
if [ -z "$output" ]; then
    output="."
fi

$JAVA_HOME/bin/java -jar ${scriptPath}/shark-test.jar -$test_type -p $platform -i ${scriptPath}/$input -o $output
#cmdStr="$JAVA_HOME/bin/java -jar ${scriptPath}/shark-test.jar -$test_type -p $platform -i ${scriptPath}/$input -o $output"
#cmdStr="java -jar ${scriptPath}/shark-test.jar -$test_type -p $platform -i ${scriptPath}/$input -o $output"

# -bench 不发邮件
#if [ "$test_type" != "bench" ]; then
#    read -p "发送至e-mail(可选):" email
#    if [ -n "$email" ]; then
#        cmdStr+=" -e StatusLogger$email"
#    fi
#fi
#echo "$cmdStr"
#exec "$cmdStr"
#echo "cd ${executingPath}"
#cd ${executingPath}