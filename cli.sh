#!/usr/bin/env bash

usage(){
    echo ""
    echo "cli.sh 脚本使用说明:"
        echo ""
        echo $0" -bench -h"
        echo "       性能测试帮助"
        echo ""
        echo $0" -func -h"
        echo "       功能测试帮助"
        echo ""
        echo $0" -usage"
        echo "       使用说明"
        echo ""
    echo ""
}

default(){
    usage
}

bench(){
   $JAVA_HOME/bin/java -jar shark-test.jar $*
}

func(){
    $JAVA_HOME/bin/java -jar shark-test.jar $*
}


if [ "$1" == "-usage" ]; then
    usage
elif [ "$1" == "-bench" ]; then
    # 函数传参数
    bench $*
elif [ "$1" == "-func" ]; then
    func $*
else
    default
fi