document.getElementById('checkBtn').addEventListener('click', checkTest);

async function checkTest() {
    const questions = document.querySelectorAll('.question');
    let score = 0;

    const answers = {
        1: "c", 2: "c", 3: "c", 4: "c", 5: "c",
        6: "c", 7: "b", 8: "b", 9: "c", 10: "b",
        11: "b", 12: "c", 13: "c", 14: "b", 15: "b",
        16: "b", 17: "d", 18: "c", 19: "d", 20: "c",
        21: "c", 22: "d", 23: "b", 24: "b", 25: "b"
    };

    questions.forEach(q => {
        const type = q.dataset.type;
        const id = q.dataset.id;
        const correct = answers[id].toLowerCase();

        if (type === "choice") {
            const selected = q.querySelector('input[type="radio"]:checked');
            if (selected && selected.value.toLowerCase() === correct) score++;
        }

        if (type === "input") {
            const input = q.querySelector('.answer-input');
            if (input && input.value.trim().toLowerCase() === correct) score++;
        }
    });

    // Определение уровня
    let level = "Новичок";
    if (score >= 9 && score <= 17) level = "Средний";
    else if (score >= 18) level = "Профи";

    const percent = Math.round((score / questions.length) * 100);

    // Показать результат пользователю
    const resultEl = document.getElementById('result');
    if (resultEl) {
        resultEl.innerText = `Ваш результат: ${score}/${questions.length} (${percent}%) — Уровень: ${level}`;
    }

    try {
        // Отправляем факт прохождения теста на сервер
        await fetch("/tests/complete", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ score: score })
        });

        // Начисляем очки пользователю
        await applyTestsPoints(score);

        // Перенаправляем на главную страницу
        window.location.href = "/";
    } catch (err) {
        console.error("Ошибка при завершении теста:", err);
        alert("Произошла ошибка при отправке результата. Попробуйте снова.");
    }
}

// Начисление очков пользователю
async function applyTestsPoints(points) {
    try {
        const sessionsResponse = await fetch('/api/sessions', { credentials: 'include' });
        if (!sessionsResponse.ok) return;

        const sessions = await sessionsResponse.json();
        const currentSession = sessions.find(s => s.username);
        if (!currentSession) return;

        await fetch(`/points/add?amount=${points}`, {
            method: 'POST',
            credentials: 'include'
        });
    } catch (err) {
        console.error('Ошибка при начислении очков:', err);
    }
}
