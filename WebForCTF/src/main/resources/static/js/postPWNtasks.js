const taskTitle = document.getElementById("taskTitle");
const submitBtn = document.getElementById("submitBtn");
const flagInput = document.getElementById("answerInput");

submitBtn.addEventListener('click', async function () {
    const flagValue = flagInput.value;
    const title = taskTitle.textContent;
    isSolved = false;
    console.log(title);
    hash = CryptoJS.SHA256(flagValue).toString();
    tasks = await loadTasks();
    for (task of tasks) {
        if (task.flag == hash) {
            isSolved = true;
            console.log("Флаг введён верно");
            window.alert("Флаг введён верно!")
            try {
                const response = await fetch(`${apiBaseUrl}/taskspwn/${title}/solve`, {
                    method: 'POST',
                    credentials: 'include'
                });
                if (response.ok) {
                    applyTestsPoints(task.points);
                }
            } catch (err) {
                console.error("Ошибка при засчитывании задания, обратитесь к администратору", err);
            }
        }
    }
    if (!isSolved) { window.alert("Флаг неверный"); }
    isSolved = false;
});

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