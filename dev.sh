#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONTAINER_NAME="pet-backend"
IMAGE_NAME="pet-backend"

cd "$ROOT_DIR"

# 外部构建后端
echo "构建后端..."
cd "$ROOT_DIR/pet-backend"
mvn -DskipTests package

# 构建 Docker 镜像
echo "构建 Docker 镜像..."
docker build -t "$IMAGE_NAME" .

# 停止并删除旧容器
docker rm -f "$CONTAINER_NAME" 2>/dev/null || true

# 启动新容器
echo "启动后端容器..."
docker run -d \
  --name "$CONTAINER_NAME" \
  --network compose_default \
  -p 8910:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/pet?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="mysql-dev-123456." \
  -e APP_UPLOAD_DIR="/data/uploads" \
  -v pet_uploads:/data/uploads \
  "$IMAGE_NAME"

cd "$ROOT_DIR"

echo "等待后端启动..."
for i in {1..60}; do
  if curl -fsS "http://129.204.27.16:8910/api/announcements/latest" >/dev/null 2>&1; then
    echo "后端已就绪: http://129.204.27.16:8910"
    break
  fi
  sleep 1
done

cd "$ROOT_DIR/pet-front"
if [ ! -d node_modules ]; then
  npm install
fi
npm run dev -- --host 0.0.0.0
