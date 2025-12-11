from flask import Flask, request
import subprocess
import os

app = Flask(__name__)

# Список разрешённых программ (можно оставить как строку или преобразовать в множество для быстрой проверки)
allowed_files = {
    "bin_10", "bin_11", "bin_12", "bin_13", "bin_14", "bin_15", "bin_16", 
    "bin_17", "bin_18", "bin_19", "bin_2", "bin_20", "bin_3", "bin_4", 
    "bin_5", "bin_6", "bin_8", "bin_9", "challenge"
}

@app.route('/progs/<filename>', methods=['GET', 'POST'])
def execute(filename):
    # Проверяем, разрешён ли файл
    if filename not in allowed_files:
        return '<h3>Ошибка: такой программы нет.</h3><a href="/">Назад</a>', 404

    # Путь к исполняемому файлу
    program_path = f'./compiled/{filename}'
    
    # Проверяем, существует ли файл и исполняем ли он
    if not os.path.isfile(program_path):
        return f'<h3>Ошибка: файл {filename} не найден.</h3>', 404
    if not os.access(program_path, os.X_OK):
        return f'<h3>Ошибка: файл {filename} не является исполняемым.</h3>', 403

    if request.method == 'GET':
        # Запуск без ввода
        try:
            result = subprocess.run([program_path], 
                                  capture_output=True, 
                                  text=True, 
                                  timeout=10)
        except subprocess.TimeoutExpired:
            return '<h3>Ошибка: программа превысила лимит времени.</h3>', 500
        except Exception as e:
            return f'<h3>Ошибка при запуске: {str(e)}</h3>', 500

        return f'''
            <h3>Результат выполнения {filename}:</h3>
            <pre>{result.stdout}</pre>
            <hr>
            <form method="POST">
                <input type="text" name="input" placeholder="Введите текст">
                <button type="submit">Отправить в программу</button>
            </form>
            <a href="/">На главную</a>
        '''

    elif request.method == 'POST':
        user_input = request.form.get('input', '')
        
        try:
            result = subprocess.run([program_path], 
                                  input=user_input,
                                  capture_output=True, 
                                  text=True,
                                  encoding='utf-8',
                                  timeout=10)
        except subprocess.TimeoutExpired:
            return '<h3>Ошибка: программа не ответила за отведённое время.</h3>', 500
        except Exception as e:
            return f'<h3>Ошибка выполнения: {str(e)}</h3>', 500

        return f'''
            <h3>Вы ввели: "{user_input}"</h3>
            <h3>Результат {filename}:</h3>
            <pre>{result.stdout}</pre>
            <a href="/progs/{filename}">Назад</a>
        '''

# Главная страница — опционально, можно вывести список доступных программ
@app.route('/')
def index():
    links = ''.join([f'<li><a href="/progs/{f}">{f}</a></li>' for f in sorted(allowed_files)])
    return f'<h2>Доступные программы:</h2><ul>{links}</ul>'

if __name__ == '__main__':
    app.run(debug=True, port=5000)