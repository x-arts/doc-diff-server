# 文档比对


# 服务启动的准备

## 文档转换工具的安装

### minerU
https://github.com/opendatalab/MinerU

### docx2md
pip install docx2md


## 修改目录配置
打开 application-local.yml  配置文件
修改 local 下的所有目录


## 脚本放置
讲 /resources/filesstore/script/ 目录下的脚本 放置到 application-local.yml 配置的script目录下

## 启动服务
直接 run  DocDiffServerApplication 的 main 方法即可