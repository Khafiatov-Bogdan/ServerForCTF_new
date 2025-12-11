# Используем официальный Python образ
FROM python:3.9-slim

# Устанавливаем системные зависимости для компиляции C программ
# RUN apt-get update && apt-get install -y \
#     gcc \
#     build-essential \
#     && rm -rf /var/lib/apt/lists/*

# Создаем рабочую директорию
WORKDIR /app

# Копируем файлы зависимостей
COPY requirements.txt .

# Устанавливаем Python зависимости
RUN pip install --no-cache-dir -r requirements.txt

# Копируем исходный код приложения
COPY app.py .

# Копируем компилированные файлы
COPY ./compiled ./compiled

# Копируем скомпилированную C программу (предполагается, что она уже скомпилирована)
# Если нужно компилировать внутри контейнера, добавь .c файлы:
# COPY program.c .
# RUN gcc -o program program.c && chmod +x program

# Если программа уже скомпилирована, просто копируй:
# COPY program .

# Открываем порт
EXPOSE 5000

# Запускаем приложение
CMD ["python", "app.py"]