#!/bin/bash

# 检查参数数量
if [ "$#" -ne 2 ]; then
  echo "用法: $0 <input_docx_file> <output_directory>"
  exit 1
fi

INPUT_FILE="$1"
OUTPUT_DIR="$2"

# 检查输入文件是否存在
if [ ! -f "$INPUT_FILE" ]; then
  echo "❌ 错误：输入文件 $INPUT_FILE 不存在"
  exit 1
fi

# 检查输出目录是否存在，不存在则创建
if [ ! -d "$OUTPUT_DIR" ]; then
  echo "🔧 输出目录 $OUTPUT_DIR 不存在，正在创建..."
  mkdir -p "$OUTPUT_DIR"
fi

# 执行转换
echo "🚀 正在转换 $INPUT_FILE 到 HTML ..."
soffice --headless --convert-to "html:HTML (StarWriter)" "$INPUT_FILE" --outdir "$OUTPUT_DIR"

# 检查转换是否成功
if [ $? -eq 0 ]; then
  echo "✅ 转换成功，HTML 文件已保存至 $OUTPUT_DIR"
else
  echo "❌ 转换失败，请检查 LibreOffice 是否已正确安装"
fi