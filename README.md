# Mini-task
个人自用任务管理工具

# Feature
- 命令行友好
- Alfred workflow 加成
- 简单够用

# Install
```bash
# download file and open it.

mkdir -p $HOME/config/minitask
cat <<EOF > $HOME/config/minitask/inbox.md
# INBOX task 1. ||| @test

---

# INBOX 用户注册 ||| @biz :priority_i: <5> 
EOF

open dist/minitask.alfredworkflow

```

# DEV
## PRE
- maven 3.3+
- jdk 1.8+
- graalvm 1.0+
- macos // need plutil, json to plist
- yq // yml to json
- /usr/local/bin/chrome-cli # brew install chrome-cli

## Build
```bash
./build.sh
```

# Showcase
## Show inbox task
![1573729262.png](https://raw.githubusercontent.com/guxingke/oss/master/blog/1573729262.png)  
Enter => mig status to next . INBOX->TODO->DOING->DONE  
Cmd + Enter => mig status to preview. DONE->DOING->TODO->INBOX  

## Show current week task
![1573729778.png](https://raw.githubusercontent.com/guxingke/oss/master/blog/1573729778.png)
![1573729811.png](https://raw.githubusercontent.com/guxingke/oss/master/blog/1573729811.png)

## Filter by fuzzy query
![1573729891.png](https://raw.githubusercontent.com/guxingke/oss/master/blog/1573729891.png)

## Cli interactive add task.
![1573730225.png](https://raw.githubusercontent.com/guxingke/oss/master/blog/1573730225.png)
  
![1573730045.png](https://raw.githubusercontent.com/guxingke/oss/master/blog/1573730045.png)

# Note
it build for myself, some cfg just for me.

# Changelog
- base available

# Ref
- [script-filter](https://www.alfredapp.com/help/workflows/inputs/script-filter/)
- [yq](https://yq.readthedocs.io/en/latest/)
- [plutil](http://www.manpagez.com/man/1/plutil/)
