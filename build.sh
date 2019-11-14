#!/usr/bin/env bash

echo 'mvn build begin'
mvn -q clean package -DskipTests
echo 'mvn build done'

echo 'aot compile begin'
native-image -cp target/minitask.jar \
  -H:Name=target/minitask \
  -H:ReflectionConfigurationFiles=reflection.json \
  -H:+ReportUnsupportedElementsAtRuntime \
  --no-server \
  com.gxk.minitask.Main
echo 'aot compile done'

# build dist
echo 'build dist wf begin'
mkdir dist temp

cp -R static temp/
cp target/minitask temp/

cp workflow.yml temp/

pushd temp

cat workflow.yml | yq . > info.plist
plutil -convert xml1 info.plist

zip -r temp.zip *

popd

mv temp/temp.zip dist/minitask.alfredworkflow

echo 'build dist wf done.'

#rm -rf temp

