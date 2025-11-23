/**
 * Web Challenges JavaScript functionality
 * Handles challenge modals, filtering, and interactions
 */

// Глобальные переменные для управления состоянием
let currentChallenge = null;
const challengeFlags = {
    'sqli': 'flag{sql_injection_success_2024}',
    'xss': 'flag{xss_successful_2024}',
    'auth': 'flag{auth_bypass_success_2024}',
    'path': 'flag{path_traversal_success_2024}',
    'csrf': 'flag{csrf_attack_success_2024}'
};

// Инициализация при загрузке DOM
document.addEventListener('DOMContentLoaded', function() {
    console.log('Web Challenges initialized');
    initializeChallengeModals();
    initializeTaskFilters();
    initializeHints();
    ensureFooterPosition();
});

// Инициализация модальных окон
function initializeChallengeModals() {
    const modal = document.getElementById('challengeModal');
    const closeBtn = document.querySelector('.close');
    
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            modal.style.display = 'none';
            currentChallenge = null;
        });
    }
    
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
            currentChallenge = null;
        }
    });
}

// Инициализация фильтров заданий
function initializeTaskFilters() {
    const filterButtons = document.querySelectorAll('.filter-btn');
    
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Убираем активный класс у всех кнопок
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Добавляем активный класс текущей кнопке
            this.classList.add('active');
            
            const filter = this.getAttribute('data-filter');
            filterTasks(filter);
        });
    });
}

// Фильтрация заданий по сложности
function filterTasks(difficulty) {
    const tasks = document.querySelectorAll('.task-card');
    const tasksList = document.querySelector('.tasks-list');
    let visibleCount = 0;
    
    // Создаем сообщение для пустого состояния, если его нет
    let noTasksMessage = document.querySelector('.no-tasks-message');
    if (!noTasksMessage) {
        noTasksMessage = document.createElement('div');
        noTasksMessage.className = 'no-tasks-message hidden';
        noTasksMessage.textContent = 'No challenges found for this difficulty level';
        tasksList.appendChild(noTasksMessage);
    }
    
    tasks.forEach(task => {
        if (difficulty === 'all' || task.getAttribute('data-difficulty') === difficulty) {
            task.style.display = 'block';
            task.style.animation = `challengeFadeIn 0.5s ease-out ${visibleCount * 0.1}s both`;
            visibleCount++;
        } else {
            task.style.display = 'none';
        }
    });
    
    // Показываем/скрываем сообщение о пустом состоянии
    if (visibleCount === 0) {
        noTasksMessage.classList.remove('hidden');
    } else {
        noTasksMessage.classList.add('hidden');
    }
    
    console.log(`Filtered tasks: ${visibleCount} visible with filter '${difficulty}'`);
    
    // Обновляем положение футера после фильтрации
    setTimeout(ensureFooterPosition, 100);
}

// Гарантирует правильное положение футера
function ensureFooterPosition() {
    const container = document.querySelector('.container');
    const tasksSection = document.querySelector('.tasks-section');
    const footer = document.querySelector('.footer');
    
    if (container && tasksSection && footer) {
        const containerHeight = container.scrollHeight;
        const windowHeight = window.innerHeight;
        
        // Если контент меньше высоты окна, футер прижимается к низу
        if (containerHeight < windowHeight) {
            footer.style.marginTop = 'auto';
        } else {
            footer.style.marginTop = '40px';
        }
    }
}

// Инициализация системы подсказок
function initializeHints() {
    console.log('Initializing hints system...');

    // Сначала скрываем все подсказки
    document.querySelectorAll('.task-hint').forEach(hint => {
        hint.style.display = 'none';
    });

    // Добавляем обработчики для всех кнопок подсказок
    const hintButtons = document.querySelectorAll('.hint-btn');

    hintButtons.forEach(button => {
        // Убираем старый обработчик onclick если есть
        button.removeAttribute('onclick');

        // Добавляем новый обработчик
        button.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();

            // Находим ID подсказки из data-атрибута
            const hintId = this.getAttribute('data-hint-id');
            console.log('Hint button clicked, hintId:', hintId);

            if (hintId) {
                toggleHint(hintId, this);
            } else {
                console.error('No data-hint-id attribute found on hint button');
            }
        });

        console.log(`Hint button initialized: ${button.getAttribute('data-hint-id')}`);
    });
    
    console.log(`Initialized ${hintButtons.length} hint buttons`);
}

// Функция переключения подсказки
function toggleHint(hintId, button) {
    const hintElement = document.getElementById(hintId);
    
    if (!hintElement) {
        console.error('Hint element not found:', hintId);
        return;
    }
    
    const isVisible = hintElement.style.display === 'block';
    
    if (!isVisible) {
        // Показываем подсказку
        hintElement.style.display = 'block';
        button.textContent = 'Hide Hint';
        button.classList.add('active');
        
        // Плавное появление
        hintElement.style.opacity = '0';
        hintElement.style.transform = 'translateY(-10px)';
        hintElement.style.transition = 'all 0.3s ease';
        
        setTimeout(() => {
            hintElement.style.opacity = '1';
            hintElement.style.transform = 'translateY(0)';
        }, 10);
        
        // Логируем использование подсказки
        logHintUsage(hintId);
    } else {
        // Скрываем подсказку
        hintElement.style.opacity = '0';
        hintElement.style.transform = 'translateY(-10px)';
        
        setTimeout(() => {
            hintElement.style.display = 'none';
            button.textContent = 'Show Hint';
            button.classList.remove('active');
        }, 300);
    }
}

// Функции для работы с заданиями
function openChallenge(challengeType) {
    console.log('Opening challenge:', challengeType);
    
    // Редирект на страницу задания
    window.location.href = `/challenges/${challengeType}`;
}

// Функция проверки флага (для модальных окон если используются)
function checkFlag(challengeType) {
    const flagInput = document.getElementById(`${challengeType}Flag`);
    const resultDiv = document.getElementById(`${challengeType}Result`);
    
    if (!flagInput || !resultDiv) {
        console.error('Flag input or result div not found');
        return;
    }
    
    const userFlag = flagInput.value.trim();
    const correctFlag = challengeFlags[challengeType];
    
    if (userFlag === correctFlag) {
        resultDiv.innerHTML = '<p class="success-message">✅ Правильно! Флаг принят.</p>';
        resultDiv.style.animation = 'challengeFadeIn 0.5s ease-out';
        
        // Логируем успешное решение
        console.log(`Challenge ${challengeType} solved with flag: ${userFlag}`);
        
        submitFlagToServer(challengeType, userFlag);
    } else {
        resultDiv.innerHTML = '<p class="error-message">❌ Неверный флаг. Попробуйте еще раз.</p>';
        resultDiv.style.animation = 'challengeFadeIn 0.3s ease-out';
        
        console.log(`Failed attempt for ${challengeType}: ${userFlag}`);
    }
}

// Заглушка для отправки флага на сервер
function submitFlagToServer(challengeType, flag) {
    console.log(`Submitting flag to server: ${challengeType} - ${flag}`);
}

// Логирование использования подсказок
function logHintUsage(hintId) {
    console.log(`Hint used: ${hintId}`);
    
    // Можно отправить на сервер для статистики
    fetch('/api/hint/used', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            hintId: hintId,
            timestamp: new Date().toISOString(),
            page: 'web'
        })
    }).catch(error => {
        console.log('Hint logging failed (might be offline)');
    });
}

// Обработчик изменения размера окна
window.addEventListener('resize', debounce(ensureFooterPosition, 250));

// Утилиты
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Добавляем CSS для подсказок если его нет
const hintStyles = document.createElement('style');
hintStyles.textContent = `
    .task-hint {
        background: rgba(255, 165, 0, 0.1);
        border: 1px solid #ffa500;
        border-radius: 8px;
        padding: 15px;
        margin-top: 10px;
        color: #ffa500;
        display: none;
    }
    
    .task-hint strong {
        color: #ff8c00;
    }
    
    .hint-btn.active {
        background: #ffa500 !important;
        color: #000 !important;
        border-color: #ffa500 !important;
    }
    
    .task-hint {
        transition: all 0.3s ease;
    }
    
    @keyframes challengeFadeIn {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
`;
document.head.appendChild(hintStyles);

// Экспорт для тестирования
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initializeChallengeModals,
        filterTasks,
        checkFlag,
        challengeFlags,
        ensureFooterPosition,
        toggleHint,
        initializeHints
    };
}