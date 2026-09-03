FROM node:22-slim AS base

RUN apt-get update && apt-get install -y --no-install-recommends \
  python3 python3-pip python3-venv \
  git curl wget unzip \
  build-essential \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

FROM base AS server-build
COPY server/package*.json ./
RUN npm install
COPY server/ .
RUN npm run build || true

FROM base
WORKDIR /app/server
COPY --from=server-build /app/node_modules ./node_modules
COPY --from=server-build /app/ .
# Web build
WORKDIR /app/web
COPY web/package*.json ./
RUN npm install
COPY web/ .
RUN npm run build || true

WORKDIR /app/server
ENV NODE_ENV=production
EXPOSE 8080
CMD ["npx", "tsx", "src/index.ts"]
