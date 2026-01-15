#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$ROOT_DIR"

docker-compose up -d --build backend

echo "等待后端启动..."
for i in {1..60}; do
  if curl -fsS "http://localhost:8910/api/announcements/latest" >/dev/null 2>&1; then
    echo "后端已就绪: http://localhost:8910"
    break
  fi
  sleep 1
done

cd "$ROOT_DIR/pet-front"
if [ ! -d node_modules ]; then
  npm install
fi
npm run dev -- --host 0.0.0.0
