# Этап 1: Сборка фронтенда
FROM node:22-alpine AS frontend-builder
WORKDIR /build
# Копируем только файлы зависимостей для кэширования
COPY frontend/package*.json ./
RUN npm ci
# Копируем исходники фронтенда и собираем статику
COPY frontend/ ./
RUN npm run build

# Этап 2: Nginx с собранной статикой
FROM nginx:alpine
# Копируем статику из предыдущего этапа
COPY --from=frontend-builder /build/dist /usr/share/nginx/html
# Копируем конфигурацию nginx (правильный путь относительно корня проекта)
COPY nginx/default.conf /etc/nginx/conf.d/default.conf