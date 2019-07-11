#!/usr/bin/env bash

#本地构建+功能测试+上传服务器
#0 prepare
scriptPath=$(cd $(dirname $0);pwd)
executingPath=$(pwd)
#scriptParentPath=$(cd $(dirname $0);cd ..; pwd)
echo "cd ${scriptPath}"
cd ${scriptPath}

#1 build
echo ""
echo "-----------------------------------------"
echo "--         Start Building...           --"
echo "-----------------------------------------"
echo ""
mvn clean assembly:assembly -DskipTest #> /dev/null 2>&1
rm -rf shark-test-out
mkdir -p shark-test-out/
cp target/shark-test-*-jar-with-dependencies.jar shark-test-out/shark-test.jar
cp shark-testcase/func/shark-func.json shark-test-out/test.json
cp func-test.sh shark-test-out/test-cli.sh
cp cli.sh shark-test-out/cli.sh

echo ""
echo "-----------------------------------------"
echo "--         Build successfully.         --"
echo "-----------------------------------------"
echo ""

#2 test

echo ""
echo "-----------------------------------------"
echo "--   Start to test shark-test...      --"
echo "-----------------------------------------"
echo ""

sh shark-test-out/test-cli.sh

echo ""
echo "-----------------------------------------"
echo "--          Test finished.             --"
echo "-----------------------------------------"
echo ""


#3 deploy
rm -rf shark-test-out/test-cli.sh
tar -czf shark-test-out.tgz shark-test-out/
#scp shark-test-out.tgz lili@127.0.0.1:/home/lili/shark-test

#4 end
echo ""
echo "-----------------------------------------"
echo "--                DONE.                --"
echo "-----------------------------------------"
echo ""
