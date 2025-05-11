#!/bin/bash

# docx2md.sh - 将 DOCX 文件转换为 Markdown
# 用法: ./docx2md.sh <input.docx> <output_dir>

# 检查参数数量
if [ "$#" -ne 2 ]; then
    echo "错误: 需要两个参数"
    echo "用法: $0 <input.docx> <output_dir>"
    exit 1
fi

input_file="$1"
output_dir="$2"

# 检查输入文件是否存在
if [ ! -f "$input_file" ]; then
    echo "错误: 输入文件 '$input_file' 不存在"
    exit 1
fi

# 检查输出目录是否存在，如果不存在则创建
if [ ! -d "$output_dir" ]; then
    echo "输出目录 '$output_dir' 不存在，正在创建..."
    mkdir -p "$output_dir"
fi

# 执行转换命令
echo "正在将 '$input_file' 转换为 Markdown 并保存到 '$output_dir'..."
docx2md "$input_file" "$output_dir"

# 检查命令是否成功执行
if [ $? -eq 0 ]; then
    echo "转换成功完成!"
else
    echo "错误: 转换失败"
    exit 1
fi