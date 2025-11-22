/**
 * SQL Injection Challenge JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {
    console.log('SQL Injection Challenge initialized');
    initializeLoginForm();
});

function initializeLoginForm() {
    const loginForm = document.getElementById('loginForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            handleLogin();
        });
    }
}

function handleLogin() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const messageDiv = document.getElementById('message');
    
    messageDiv.innerHTML = '<div class="success">🔐 Проверяем credentials...</div>';
    
    fetch('/challenges/sqli/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            messageDiv.innerHTML = `<div class="success">✅ ${data.message}</div>`;
            // ПОКАЗЫВАЕМ ФЛАГ ПОЛЬЗОВАТЕЛЮ
            if (data.flag) {
                messageDiv.innerHTML += `<div class="success" style="margin-top: 10px;">
                    <strong>🎉 Флаг:</strong> ${data.flag}
                </div>`;
            }
            showFlagSection();
        } else {
            messageDiv.innerHTML = `<div class="error">❌ ${data.message}</div>`;
        }
    })
    .catch(error => {
        messageDiv.innerHTML = '<div class="error">⚠️ Ошибка соединения с сервером</div>';
    });
}

function showFlagSection() {
    const flagSection = document.getElementById('flagSection');
    flagSection.style.display = 'block';
}

function validateFlag() {
    const flag = document.getElementById('flagInput').value.trim();
    const resultDiv = document.getElementById('validationResult');
    
    if (!flag) {
        resultDiv.innerHTML = '<div class="error">⚠️ Введите флаг для проверки</div>';
        return;
    }
    
    resultDiv.innerHTML = '<div class="success">🔍 Проверяем флаг...</div>';
    
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
            resultDiv.innerHTML = `<div class="success">🎉 ${data.message}</div>`;
            celebrateSuccess();
        } else {
            resultDiv.innerHTML = `<div class="error">❌ ${data.message}</div>`;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        resultDiv.innerHTML = '<div class="error">⚠️ Ошибка проверки флага</div>';
    });
}

function logSuccess(username) {
    console.log(`SQL Injection successful with username: ${username}`);
}

function logFailedAttempt(username) {
    console.log(`Failed login attempt: ${username}`);
}

function celebrateSuccess() {
    // Простая анимация успеха
    const flagSection = document.getElementById('flagSection');
    flagSection.style.transform = 'scale(1.05)';
    setTimeout(() => {
        flagSection.style.transform = 'scale(1)';
    }, 300);
    
    createConfetti();
}

function createConfetti() {
    const colors = ['#00ff88', '#ff4444', '#4488ff', '#ffff00', '#ff00ff'];
    
    for (let i = 0; i < 20; i++) {
        setTimeout(() => {
            const confetti = document.createElement('div');
            confetti.style.cssText = `
                position: fixed;
                width: 8px;
                height: 8px;
                background: ${colors[Math.floor(Math.random() * colors.length)]};
                top: -10px;
                left: ${Math.random() * 100}%;
                animation: confettiFall ${Math.random() * 2 + 1}s linear forwards;
                pointer-events: none;
                z-index: 1000;
            `;
            
            document.body.appendChild(confetti);
            
            setTimeout(() => {
                confetti.remove();
            }, 2000);
        }, i * 100);
    }
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
`;
document.head.appendChild(confettiStyles);

// Экспорт для тестирования
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        handleLogin,
        validateFlag,
        showFlagSection
    };
}