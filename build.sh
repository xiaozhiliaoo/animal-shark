#!/usr/bin/env bash

# 本地构建
#0 prepare
scriptPath=$(cd $(dirname $0);pwd)
executingPath=$(pwd)
echo "cd ${scriptPath}"
cd ${scriptPath}

#1 build
echo ""
echo "-----------------------------------------"
echo "--         Start Building...           --"
echo "-----------------------------------------"
echo ""
mvn clean assembly:assembly -DskipTest
cp target/animal-shark-0.0.1-SNAPSHOT-jar-with-dependencies.jar shark-test.jar


echo ""
echo "-----------------------------------------"
echo "--         Building Successful         --"
echo "-----------------------------------------"
echo ""



