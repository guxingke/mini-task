#!/usr/bin/env bash

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

