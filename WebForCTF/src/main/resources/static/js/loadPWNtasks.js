async function loadTasks() {
    let tasks = []
    try {
        const response = await fetch(`${apiBaseUrl}/taskspwn/available`);
        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }
        tasks = await response.json();
        return tasks;
    } catch (error) {
        console.error('Ошибка загрузки задач:', error);
        tasksList.innerHTML = '<div class="error">Ошибка загрузки задач</div>';
    }
}