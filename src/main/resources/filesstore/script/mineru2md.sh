#!/bin/zsh

# 检查是否传入 PDF 文件路径参数
if [[ -z "$1" ]]; then
  echo "❌ 请传入要转换的 PDF 文件路径作为第一个参数"
  echo "用法: ./run_magic_pdf_zsh.sh your_file.pdf /your/output/dir/"
  exit 1
fi

# 检查是否传入输出目录参数
if [[ -z "$2" ]]; then
  echo "❌ 请传入输出目录作为第二个参数"
  echo "用法: ./run_magic_pdf_zsh.sh your_file.pdf /your/output/dir/"
  exit 1
fi

# 设置变量
PDF_FILE="$1"
OUTPUT_DIR="$2"

# 激活 conda 环境（zsh）
source ~/.zshrc
conda activate mineru

# 执行命令
echo "🚀 正在执行：magic-pdf -p $PDF_FILE -o $OUTPUT_DIR -m auto"
magic-pdf -p "$PDF_FILE" -o "$OUTPUT_DIR" -m auto

# 退出 conda 环境
conda deactivate
