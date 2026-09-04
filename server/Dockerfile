# VirgoYT Cloud AI — backend server image
FROM node:22-alpine

WORKDIR /app

# Install deps (includes tsx, needed to run TS directly)
COPY package.json package-lock.json* ./
RUN npm install --no-audit --no-fund

COPY tsconfig.json ./
COPY src ./src

EXPOSE 8080
ENV PORT=8080
CMD ["npx", "tsx", "src/index.ts"]
