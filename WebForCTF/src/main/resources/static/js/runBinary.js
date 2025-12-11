const execButton = document.getElementById("execButton");
const execCommand = document.getElementById("execCommand");
const details = document.getElementById('taskDetails');

execButton.addEventListener("click", async () => {
    const tasks = await loadTasks();
    let data = execCommand.value;
    let taskTitle = details.querySelector('.task-title').textContent;
    result = document.getElementById("result");
   

    index = tasks.findIndex(item => item.title === taskTitle);
    binaryName = tasks[index].binary;

    const response = await fetch(`http://localhost:8081/run/${binaryName}`, {
        method: "POST",
        headers: {
            "Content-Type": "text/plain"
        },
        body: data
    });

    const answer = await response.text();
    result.innerHTML = answer;
});