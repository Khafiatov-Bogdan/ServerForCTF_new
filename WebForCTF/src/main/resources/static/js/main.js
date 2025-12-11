/**
 * CTF Platform - Main JavaScript
 * Инициализация приложения и общая логика
 */

class CTFPlatform {
    constructor() {
        this.currentPage = window.location.pathname;
        this.init();
    }

    init() {
        console.log('🚀 CTF Platform initialized on:', this.currentPage);
        this.initNavigation();
        this.initTheme();
        this.initParticles();
        this.initScrollEffects();
        this.initEventListeners();
        this.initPageSpecific();
    }

    initNavigation() {
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.nav-link');

        navLinks.forEach(link => {
            const linkPath = link.getAttribute('href');
            const isLoginBtn = link.classList.contains('login-btn');

            link.classList.remove('active');

            if (isLoginBtn) {
                if (currentPath === '/auth') link.classList.add('active');
            } else if (linkPath) {
                if (currentPath === linkPath) link.classList.add('active');
                else if (currentPath.startsWith('/category/') && linkPath === currentPath) link.classList.add('active');
                else if (currentPath.startsWith('/challenges/') && linkPath === currentPath) link.classList.add('active');
                else if (currentPath === '/' && linkPath === '/') link.classList.add('active');
                else if (currentPath === '/users' && linkPath === '/users') link.classList.add('active');
            }
        });
    }

    initTheme() {
        const savedTheme = localStorage.getItem('ctf-theme');
        if (savedTheme) document.documentElement.setAttribute('data-theme', savedTheme);
    }

    initParticles() {
        const particlesContainer = document.querySelector('.particles');
        if (!particlesContainer) return;
        particlesContainer.innerHTML = '';

        const particleCount = 8;
        for (let i = 0; i < particleCount; i++) this.createParticle(particlesContainer, i);
    }

    createParticle(container, index) {
        const particle = document.createElement('div');
        particle.className = 'particle';

        const size = Math.random() * 6 + 2;
        const posX = Math.random() * 100;
        const posY = Math.random() * 100;
        const delay = Math.random() * 5;
        const duration = Math.random() * 10 + 5;

        Object.assign(particle.style, {
            width: `${size}px`,
            height: `${size}px`,
            top: `${posY}%`,
            left: `${posX}%`,
            animationDelay: `${delay}s`,
            animationDuration: `${duration}s`,
            background: index % 3 === 0 ? 'var(--primary-color)' :
                       index % 3 === 1 ? 'var(--secondary-color)' : 'var(--accent-color)'
        });

        container.appendChild(particle);
    }

    initScrollEffects() {
        const background = document.querySelector('.background');
        if (background) {
            background._mouseMoveHandler && document.removeEventListener('mousemove', background._mouseMoveHandler);
            background._mouseMoveHandler = (e) => {
                const x = e.clientX / window.innerWidth;
                const y = e.clientY / window.innerHeight;
                background.style.transform = `translate(${x * 20}px, ${y * 20}px)`;
            };
            window.addEventListener('mousemove', background._mouseMoveHandler);
        }

        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            });
        });
    }

    initEventListeners() {
        document.addEventListener('click', (e) => {
            const challengeCard = e.target.closest('.challenge-card');
            if (challengeCard && challengeCard.getAttribute('data-href')) {
                e.preventDefault();
                window.location.href = challengeCard.getAttribute('data-href');
            }
        });
    }

    initPageSpecific() {
        const path = window.location.pathname;
        if (path === '/') this.initHomePage();
        else if (path === '/users') this.initUsersPage();
        else if (path.includes('/category/')) this.initCategoryPage();
        else if (path === '/auth') this.initAuthPage();
        else if (path.includes('/challenges/')) this.initChallengePage();
    }

    initHomePage() {
        this.initTerminal();
        this.initLeaderboard();
        this.initCategoryCards();
    }

    initTerminal() {
        // оставляем как есть
    }

    getCurrentText(messages, currentMessage) {
        let text = '';
        for (let i = 0; i < currentMessage; i++) text += messages[i] + '<br>';
        return text;
    }

    initLeaderboard() {
        console.log('initLeaderboard called');
        // generateTop3Leaderboard(); // <-- убедись, что функция глобально определена
    }

    initCategoryCards() {
        const cards = document.querySelectorAll('.category-card, .challenge-card');
        cards.forEach(card => {
            card._mouseEnterHandler && card.removeEventListener('mouseenter', card._mouseEnterHandler);
            card._mouseLeaveHandler && card.removeEventListener('mouseleave', card._mouseLeaveHandler);

            card._mouseEnterHandler = () => card.style.transform = 'translateY(-10px)';
            card._mouseLeaveHandler = () => card.style.transform = 'translateY(0)';

            card.addEventListener('mouseenter', card._mouseEnterHandler);
            card.addEventListener('mouseleave', card._mouseLeaveHandler);
        });
    }

    initUsersPage() { }
    initCategoryPage() { this.initCategoryCards(); }
    initAuthPage() { }
    initChallengePage() { }

    async checkPromoCode() {
        const input = document.querySelector('#promoInput');
        if (!input) {
            console.log('Поле ввода промокода не найдено');
            return;
        }

        const code = input.value.trim();
        if (!code) {
            console.log('Промокод пустой');
            return;
        }

        console.log('Отправка промокода на сервер:', code);

        try {
            // 1. Проверка промокода на сервере
            const promoResponse = await fetch('http://localhost:8081/promo/use', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ code })
            });

            console.log('Ответ от /promo/use:', promoResponse.status);

            const promoData = await promoResponse.json();
            console.log('Данные ответа /promo/use:', promoData);

            const messageElement = document.querySelector('#promoMessage');
            if (messageElement) {
                messageElement.textContent = promoData.message;
                messageElement.style.color = promoData.success ? 'green' : 'red';
            }
             const points = promoData.points;
             console.log(`Промокод валиден, начисляем очки: ${points}`);
            if (!promoData.success) {
                console.log('Промокод не прошел проверку');
                return;
            }



            // 2. Получаем активного пользователя
            const sessionsResponse = await fetch('/api/sessions', { credentials: 'include' });
            const sessions = await sessionsResponse.json();
            console.log('Сессии пользователя:', sessions);

            const currentSession = sessions.find(s => s.username);
            if (!currentSession) {
                console.error('Сессия пользователя не активна');
                return;
            }

            const sessionUsername = currentSession.username;
            console.log('Текущий пользователь:', sessionUsername);

            // 3. Начисляем очки через фронт-контроллер
            const addPointsResponse = await fetch(`/points/add?amount=${points}`, {
                method: 'POST',
                credentials: 'include'
            });

            console.log('Ответ от /points/add:', addPointsResponse.status);

            if (addPointsResponse.ok) {
                console.log(`Начислено ${points} очков пользователю ${sessionUsername}`);
            } else {
                const errorData = await addPointsResponse.json().catch(() => null);
                console.error('Ошибка при начислении очков:', errorData);
            }

        } catch (e) {
            console.error('Ошибка при проверке промокода', e);
        }
    }


}
    // Вспомогательная функция для получения куки по имени
    function getCookie(name) {
        const matches = document.cookie.match(new RegExp(
            "(?:^|; )" + name.replace(/([.$?*|{}()[\]\\/+^])/g, '\\$1') + "=([^;]*)"
        ));
        return matches ? decodeURIComponent(matches[1]) : undefined;
    }




/* ==============================
   ИНИЦИАЛИЗАЦИЯ ПЛАТФОРМЫ
============================== */
let ctfPlatformInstance = null;

function initializeCTFPlatform() {
    ctfPlatformInstance = new CTFPlatform();
}

document.addEventListener('DOMContentLoaded', initializeCTFPlatform);
window.addEventListener('popstate', initializeCTFPlatform);

window.checkPromoCode = () => {
    if (ctfPlatformInstance) ctfPlatformInstance.checkPromoCode();
};
