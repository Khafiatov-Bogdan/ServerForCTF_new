async function loadUsers() {
    const container = document.getElementById('userList');
    container.innerHTML = 'Загрузка...';

    try {
        console.log('Запрос всех пользователей...');
        const resUsers = await fetch('http://localhost:8081/allNames');
        const users = await resUsers.json();
        console.log('Полученные пользователи:', users);

        if (!Array.isArray(users) || users.length === 0) {
            container.innerHTML = '<p>Нет пользователей</p>';
            return;
        }

        const maxPoints = await getMaxPoints();
        const maxLabPoints = users.length ? Math.max(...users.map(u => u.pointsLab ?? 0)) : 0;

        container.innerHTML = '';

        // Создаём карточки пользователей
        users.forEach(user => {
            const userName = user.name;

            const userPoints = user.points ?? 0;
            const userLab = user.pointsLab ?? 0;

            // Счёт процентов
            const percentPoints = maxPoints > 0 ? userPoints / maxPoints : 0;
            const percentLab = maxLabPoints > 0 ? userLab / maxLabPoints : 0;
            const percent = ((percentPoints + percentLab) / 2 * 100).toFixed(2);

            let totalScore = 2;
            if (percent >= 85) totalScore = 5;
            else if (percent >= 70) totalScore = 4;
            else if (percent >= 65) totalScore = 3;

            const card = document.createElement('div');
            card.className = 'user-card';

            card.innerHTML = `
                <span><b>${userName}</b></span>
                <div class="points-info">
                    <span>Points: <span id="points-${userName}">${userPoints}</span></span>
                    <span>Lab Points: <span id="lab-${userName}">${userLab}</span></span>
                    <span>Percent: <span id="percent-${userName}">${percent}</span></span>
                    <span>Total: <span id="total-${userName}">${totalScore}</span></span>
                </div>

                <div class="leader-actions">
                    <input type="number" id="input-${userName}" placeholder="Баллы">
                    <button id="btn-${userName}">Очки</button>

                    <input type="number" id="lab-input-${userName}" placeholder="Лаб Баллы">
                    <button id="btn-lab-${userName}">Лаба</button>
                </div>
            `;

            container.appendChild(card);

            // Обработчик обычных очков
            document.getElementById(`btn-${userName}`)
                .addEventListener('click', () => applyPoints(userName));

            // Обработчик лаб-очков
            document.getElementById(`btn-lab-${userName}`)
                .addEventListener('click', () => applyLabPoints(userName));
        });

        // Кнопка "Сохранить всех"
        const saveBtn = document.createElement('button');
        saveBtn.id = 'btn_save_users';
        saveBtn.textContent = 'Сохранить всех';
        container.appendChild(saveBtn);
        const exitBtn = document.createElement('button');
        exitBtn.id = 'btn_exit';
        exitBtn.textContent = 'Выйти';
        container.appendChild(exitBtn);

        exitBtn.addEventListener('click', () => {
            users.forEach(u => exit());
        });
        saveBtn.addEventListener('click', () => {
            users.forEach(u => saveUser(u.name));
        });

    } catch (err) {
        console.error('Ошибка в loadUsers:', err);
        container.innerHTML = '<p>Ошибка загрузки пользователей</p>';
    }
}
async function exit() {
    try {
        const res = await fetch('http://localhost:3000/logout', {
            method: 'GET',
            credentials: 'include'
        });

        // После редиректа Spring вернёт HTML — это нормально
        if (res.ok) {
            window.location.href = '/'; // Перейти на главную
        }
    } catch (err) {
        console.error('Ошибка logout:', err);
    }
}



async function saveUser(username) {
    const percentSpan = document.getElementById(`percent-${username}`);
    const totalSpan = document.getElementById(`total-${username}`);

    if (!percentSpan || !totalSpan) {
        console.error('Элементы для percent или total не найдены:', username);
        return;
    }

    const percent = parseFloat(percentSpan.textContent) || 0;
    const grade = parseInt(totalSpan.textContent) || 0;

    const report = {
        name: username,
        percent: percent,
        grade: grade
    };

    console.log('Отправка Report на сервер:', report);

    try {
        const res = await fetch('http://localhost:8081/report/save', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(report)
        });

        if (!res.ok) throw new Error('Ошибка при сохранении');

        const data = await res.json();
        console.log('Сохранено:', data);

    } catch (error) {
        console.error('Ошибка сохранения:', error);
        alert(`Ошибка сохранения отчёта для ${username}`);
    }
}



// Максимальный балл обычных очков через top3
async function getMaxPoints() {
    try {
        const res = await fetch('http://localhost:8081/top3');
        const top3 = await res.json();

        if (!Array.isArray(top3) || top3.length === 0) return 0;

        return Math.max(...top3.map(user => user.points ?? 0));
    } catch (err) {
        console.error('Ошибка получения топ-3 обычных очков:', err);
        return 0;
    }
}



// Обновление Обычных очков
async function applyPoints(username) {
    const inputEl = document.getElementById(`input-${username}`);
    const delta = parseInt(inputEl.value);

    if (isNaN(delta) || delta === 0) {
        alert('Введите число, отличное от 0');
        return;
    }

    const url = `http://localhost:3000/points/update?username=${username}&amount=${delta}`;

    try {
        const res = await fetch(url, {
            method: 'POST',
            credentials: 'include'
        });

        let data;
        const ct = res.headers.get("content-type");
        if (ct && ct.includes("application/json")) {
            data = await res.json();
        } else {
            const text = await res.text();
            alert(text);
            return;
        }

        inputEl.value = '';
        await loadUsers();

    } catch (err) {
        console.error(err);
        alert('Ошибка при изменении очков');
    }
}



// Обновление Лаб-очков (НОВАЯ ФУНКЦИЯ)
async function applyLabPoints(username) {
    const inputEl = document.getElementById(`lab-input-${username}`);
    const delta = parseInt(inputEl.value);

    if (isNaN(delta) || delta === 0) {
        alert('Введите число, отличное от 0');
        return;
    }

    const url = `http://localhost:3000/points/updateLab?username=${username}&amount=${delta}`;

    try {
        const res = await fetch(url, {
            method: 'POST',
            credentials: 'include'
        });

        let data;
        const ct = res.headers.get("content-type");
        if (ct && ct.includes("application/json")) {
            data = await res.json();
        } else {
            const text = await res.text();
            alert(text);
            return;
        }

        inputEl.value = '';
        await loadUsers();

    } catch (err) {
        console.error(err);
        alert('Ошибка при изменении ЛАБ-очков');
    }
}



document.addEventListener('DOMContentLoaded', loadUsers);
