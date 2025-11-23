/**
 * SQL Injection Challenge JavaScript
 */

let challengePoints = 100;

document.addEventListener('DOMContentLoaded', function() {
    console.log('SQL Injection Challenge initialized');
    initializeLoginForm();
    loadChallengePoints();
});

function loadChallengePoints() {
    fetch('/challenges/sqli/info')
        .then(response => response.json())
        .then(data => {
            if (data.points) {
                challengePoints = data.points;
            }
        })
        .catch(error => {
            console.log('Using default points:', challengePoints);
        });
}

function initializeLoginForm() {
    const loginForm = document.getElementById('loginForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleLogin();
        });
    }
}

function toggleHint() {
    const hintContent = document.getElementById('hintContent');
    const hintButton = document.querySelector('.hint-button');
    
    if (hintContent.classList.contains('show')) {
        hintContent.classList.remove('show');
        hintButton.textContent = 'Show Hint';
    } else {
        hintContent.classList.add('show');
        hintButton.textContent = 'Hide Hint';
        logHintUsage();
    }
}

function handleLogin() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const messageDiv = document.getElementById('message');
    
    // Валидация пустых полей
    if (!username || !password) {
        showMessage('⚠️ Пожалуйста, заполните все поля', 'warning');
        return;
    }
    
    // Показываем загрузку
    showMessage('🔐 Проверяем credentials...', 'info');
    
    fetch('/challenges/sqli/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            // Успешная SQL инъекция
            showMessage(`
                ✅ ${data.message}<br><br>
                🎉 Задание выполнено!<br>
                <strong>Флаг:</strong> 
                <div class="flag-text">${data.flag}</div>
                <div class="points-badge">+${challengePoints} очков</div>
            `, 'success');
            celebrateSuccess();
            logSuccess(username);
            
            // Автоматически проверяем флаг
            validateFlagAutomatically(data.flag);
        } else {
            // Неверные данные
            showMessage(`❌ ${data.message}<br><br>💡 Попробуйте использовать SQL инъекцию`, 'error');
            logFailedAttempt(username);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('⚠️ Ошибка соединения с сервером. Попробуйте еще раз.', 'error');
    });
}

function showMessage(message, type = 'info') {
    const messageDiv = document.getElementById('message');
    messageDiv.innerHTML = `<div class="${type}">${message}</div>`;
    
    // Добавляем анимацию
    messageDiv.style.animation = 'fadeIn 0.3s ease-out';
}

function validateFlagAutomatically(flag) {
    fetch('/challenges/sqli/validate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `flag=${encodeURIComponent(flag)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            console.log('Flag automatically validated successfully');
        }
    })
    .catch(error => {
        console.error('Error auto-validating flag:', error);
    });
}

function celebrateSuccess() {
    const loginForm = document.querySelector('.login-form');
    loginForm.classList.add('celebrate');
    
    setTimeout(() => {
        loginForm.classList.remove('celebrate');
    }, 500);
    
    createConfetti();
}

function createConfetti() {
    const colors = ['#00ff88', '#ff4444', '#4488ff', '#ffff00', '#ff00ff'];
    
    for (let i = 0; i < 25; i++) {
        setTimeout(() => {
            const confetti = document.createElement('div');
            confetti.style.cssText = `
                position: fixed;
                width: 10px;
                height: 10px;
                background: ${colors[Math.floor(Math.random() * colors.length)]};
                top: -10px;
                left: ${Math.random() * 100}%;
                animation: confettiFall ${Math.random() * 2 + 1}s linear forwards;
                pointer-events: none;
                z-index: 1000;
                border-radius: 2px;
            `;
            
            document.body.appendChild(confetti);
            
            setTimeout(() => {
                confetti.remove();
            }, 2000);
        }, i * 80);
    }
}

function logSuccess(username) {
    console.log(`SQL Injection successful with username: ${username}`);
}

function logFailedAttempt(username) {
    console.log(`Failed login attempt: ${username}`);
}

function logHintUsage() {
    console.log('Hint used for SQL Injection challenge');
}

// Добавляем стили для конфетти
const confettiStyles = document.createElement('style');
confettiStyles.textContent = `
    @keyframes confettiFall {
        0% {
            transform: translateY(0) rotate(0deg);
            opacity: 1;
        }
        100% {
            transform: translateY(100vh) rotate(360deg);
            opacity: 0;
        }
    }
    
    .message .info {
        background: rgba(0, 136, 255, 0.2);
        color: #0088ff;
        border: 1px solid #0088ff;
    }
`;
document.head.appendChild(confettiStyles);