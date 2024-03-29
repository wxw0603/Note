# Git

## 常用命令

| 命令名称                             | 作用                   |
| ------------------------------------ | ---------------------- |
| git config --global user.name 用户名 | 设置用户前面           |
| git config --global user.email 邮箱  | 设置用户邮箱           |
| git init                             | 初始化本地库           |
| git status                           | 查看本地库状态         |
| git add                              | 工作区文件添加到暂存区 |
| git rm --cached 文件名               | 删除暂存区文件         |
| git commit -m "日志信息"  文件名     | 提交本地库             |
| git reflog                           | 查看引用日志信息       |
| git log                              | 查看详细日志信息       |
| git reset --hard 版本号              | 版本穿梭               |

## 分支操作

版本控制中同时推进多个任务

| 命令名称            | 作用                     |
| ------------------- | ------------------------ |
| git branch 分支名   | 创建分支                 |
| git branch -v       | 查看分支                 |
| git checkout 分支名 | 切换分支                 |
| git merge 分支名    | 把指定分支合并到当前分支 |
|                     |                          |

## 冲突合并

两个分支对在同一个文件的同一个位置有两套完全不同的修改，需要认为决定去用哪一个

会进入mergeing状态

要人为修改然后添加暂存区并提交本地库

### 细节：此次提交不能带文件名，提交完成后离开mergeing状态

## 团队协作

| 命令名称                | 作用               |
| ----------------------- | ------------------ |
| git remote -v           | 查看当前有哪些别名 |
| git remote add name url | 创建别名           |
| git push 别名 分支名    | 推送远程库         |
| git pull 别名 分支名    | 拉取远程库         |
| git clone 别名          | 克隆远程库         |
|                         |                    |
|                         |                    |

## SSH免密登录

| 命令名称                | 作用    |
| ----------------------- | ------- |
| ssh -t 加密算法 -C 描述 | 创建ssh |
|                         |         |

## 常规的git忽略文件

git.ignore放在家目录下

```xml
#Compiled class file
*.class

#Log file

*.log

# BlueJ files
*.ctxt

#Mobile Tools for Java(J2ME)
.mtj.tmp/

#Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

.classpath
.project
.settings
target
.idea
*.iml
```

在gitconfig中配置

```xml
[core]
	excludesfile = C:/  绝对路径
```

