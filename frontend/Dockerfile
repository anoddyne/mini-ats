FROM node:22-alpine AS builder
WORKDIR /app

# Копируем файлы зависимостей и устанавливаем их
COPY package*.json ./
RUN npm ci

# Копируем исходный код и собираем приложение
COPY . .
RUN npm run build

# Итоговый образ содержит только собранную статику
FROM alpine:latest AS static
COPY --from=builder /app/dist /app/dist