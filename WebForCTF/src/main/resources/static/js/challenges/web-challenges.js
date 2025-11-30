/**
 * CTF Platform - Web Challenges Manager
 * Унифицированная система для всех веб-заданий
 */

class WebChallengesManager {
    constructor() {
        this.currentChallenge = null;
        this.init();
    }

    init() {
        console.log('🔧 WebChallengesManager initialized');
        this.initGlobalHandlers();
        this.loadChallengeProgress();
    }

    initGlobalHandlers() {
        console.log('🔧 Initializing global handlers');


        document.addEventListener('click', (e) => {
            const button = e.target.closest('button');
            if (!button) return;

            const buttonText = button.textContent;


            if (button.closest('.challenge-modal')) {
                console.log('🔕 Ignoring button inside modal:', buttonText);
                return;
            }

            console.log('🖱️ Button clicked:', buttonText);

            if (buttonText.includes('Подсказка') || buttonText.includes('Hint')) {
                const challengeName = this.getCurrentChallengeName();
                console.log('💡 Подсказка button clicked for:', challengeName);
                this.showHint(challengeName);
                e.preventDefault();
                e.stopPropagation();
            }

            if (buttonText.includes('Проверить флаг') || buttonText.includes('Check Flag') || buttonText.includes('Validate')) {
                const challengeName = this.getCurrentChallengeName();
                console.log('🏴‍☠️ Проверить флаг button clicked for:', challengeName);
                this.showFlagValidationModal(challengeName);
                e.preventDefault();
                e.stopPropagation();
            }
        });


        this.fixLegacyButtons();
    }


    fixLegacyButtons() {
        console.log('🔧 Fixing legacy buttons');


        const buttons = document.querySelectorAll('button[onclick*="validateChallengeFlag"], button[onclick*="showChallengeHint"]');

        buttons.forEach(button => {
            const onclick = button.getAttribute('onclick');
            console.log('🔧 Processing legacy button:', onclick);

            if (onclick.includes('validateChallengeFlag')) {

                const match = onclick.match(/validateChallengeFlag\('([^']+)'\)/);
                if (match) {
                    const challengeName = match[1];
                    button.onclick = null;
                    button.addEventListener('click', (e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        console.log('🏴‍☠️ Legacy validate button clicked for:', challengeName);
                        this.showFlagValidationModal(challengeName);
                    });
                }
            }

            if (onclick.includes('showChallengeHint')) {

                const match = onclick.match(/showChallengeHint\('([^']+)'\)/);
                if (match) {
                    const challengeName = match[1];
                    button.onclick = null;
                    button.addEventListener('click', (e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        console.log('💡 Legacy hint button clicked for:', challengeName);
                        this.showHint(challengeName);
                    });
                }
            }
        });
    }

    getCurrentChallengeName() {

        const path = window.location.pathname;
        let challengeName = 'Unknown Challenge';

        if (path.includes('/xss')) challengeName = 'XSS Challenge';
        else if (path.includes('/sqli')) challengeName = 'SQL Injection Basic';
        else if (path.includes('/auth-bypass')) challengeName = 'Authentication Bypass';
        else if (path.includes('/csrf')) challengeName = 'CSRF Challenge';
        else if (path.includes('/path-traversal')) challengeName = 'Path Traversal';

        console.log('📍 Current challenge detected:', challengeName, 'from path:', path);
        return challengeName;
    }


    createChallengeModal(title, content, buttons = []) {
        console.log('📦 Creating modal:', title);

        const modal = document.createElement('div');
        modal.className = 'challenge-modal';
        modal.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
            backdrop-filter: blur(10px);
            animation: fadeIn 0.3s ease-out;
        `;

        const modalContent = document.createElement('div');
        modalContent.className = 'challenge-modal-content';
        modalContent.style.cssText = `
            background: linear-gradient(135deg, rgba(26, 26, 26, 0.95), rgba(40, 40, 40, 0.95));
            border: 2px solid var(--primary-color);
            border-radius: 20px;
            padding: 2.5rem;
            max-width: 600px;
            width: 90%;
            max-height: 80vh;
            overflow-y: auto;
            color: var(--text-primary);
            backdrop-filter: blur(20px);
            box-shadow: 0 25px 80px rgba(0, 255, 136, 0.3);
            animation: slideInUp 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
            position: relative;
        `;

        const closeBtn = document.createElement('button');
        closeBtn.innerHTML = '&times;';
        closeBtn.style.cssText = `
            position: absolute;
            top: 1rem;
            right: 1rem;
            background: none;
            border: none;
            color: var(--text-secondary);
            font-size: 2rem;
            cursor: pointer;
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            transition: all 0.3s ease;
        `;

        closeBtn.addEventListener('mouseenter', () => {
            closeBtn.style.background = 'rgba(255, 255, 255, 0.1)';
            closeBtn.style.color = 'var(--primary-color)';
        });

        closeBtn.addEventListener('mouseleave', () => {
            closeBtn.style.background = 'none';
            closeBtn.style.color = 'var(--text-secondary)';
        });

        closeBtn.addEventListener('click', () => {
            console.log('❌ Modal closed');
            modal.remove();
        });

        const titleElement = document.createElement('h2');
        titleElement.textContent = title;
        titleElement.style.cssText = `
            color: var(--primary-color);
            font-family: 'Orbitron', sans-serif;
            margin-bottom: 1.5rem;
            text-align: center;
            font-size: 1.8rem;
            text-shadow: 0 0 20px rgba(0, 255, 136, 0.5);
        `;

        const contentElement = document.createElement('div');
        contentElement.className = 'modal-content';
        contentElement.innerHTML = content;

        modalContent.appendChild(closeBtn);
        modalContent.appendChild(titleElement);
        modalContent.appendChild(contentElement);


        if (buttons.length > 0) {
            const buttonsContainer = document.createElement('div');
            buttonsContainer.className = 'modal-buttons';
            buttonsContainer.style.cssText = `
                display: flex;
                gap: 1rem;
                justify-content: center;
                margin-top: 2rem;
                flex-wrap: wrap;
            `;

            buttons.forEach(buttonConfig => {
                const button = document.createElement('button');
                button.textContent = buttonConfig.text;
                button.className = buttonConfig.className || 'cta-btn primary';
                button.style.cssText = buttonConfig.style || '';

                if (buttonConfig.onClick) {
                    button.addEventListener('click', () => {
                        console.log('🔄 Button clicked:', buttonConfig.text);
                        buttonConfig.onClick();
                        if (buttonConfig.closeModal !== false) {
                            modal.remove();
                        }
                    });
                } else {
                    button.addEventListener('click', () => {
                        console.log('❌ Cancel button clicked');
                        modal.remove();
                    });
                }

                buttonsContainer.appendChild(button);
            });

            modalContent.appendChild(buttonsContainer);
        }

        modal.appendChild(modalContent);


        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                console.log('❌ Modal closed by backdrop click');
                modal.remove();
            }
        });

        document.body.appendChild(modal);
        console.log('✅ Modal created and appended to body');


        if (!document.querySelector('#modal-styles')) {
            const style = document.createElement('style');
            style.id = 'modal-styles';
            style.textContent = `
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }

                @keyframes slideInUp {
                    from {
                        opacity: 0;
                        transform: translateY(30px) scale(0.9);
                    }
                    to {
                        opacity: 1;
                        transform: translateY(0) scale(1);
                    }
                }

                .challenge-modal-content::-webkit-scrollbar {
                    width: 8px;
                }

                .challenge-modal-content::-webkit-scrollbar-track {
                    background: rgba(255, 255, 255, 0.05);
                    border-radius: 4px;
                }

                .challenge-modal-content::-webkit-scrollbar-thumb {
                    background: var(--primary-color);
                    border-radius: 4px;
                }
            `;
            document.head.appendChild(style);
        }

        return modal;
    }

    showFlagValidationModal(challengeName) {
        console.log('🏴‍☠️ Showing flag validation modal for:', challengeName);

        const modal = this.createChallengeModal(
            '🔍 Проверка флага',
            `
                <div style="text-align: center; margin-bottom: 2rem;">
                    <div style="font-size: 3rem; margin-bottom: 1rem;">🏴‍☠️</div>
                    <p style="color: var(--text-secondary); margin-bottom: 2rem;">
                        Введите флаг для задания <strong>${challengeName}</strong>
                    </p>
                    <div class="form-group">
                        <input type="text"
                               id="flagInput"
                               placeholder="CTF{...}"
                               class="form-input"
                               style="width: 100%; padding: 1rem; font-size: 1.1rem; text-align: center;">
                    </div>
                    <div id="flagMessage" style="margin-top: 1rem;"></div>
                </div>
            `,
            [
                {
                    text: '✅ Проверить флаг',
                    className: 'cta-btn primary full-width',
                    onClick: () => {
                        console.log('🔄 Validate flag button in modal clicked');
                        this.submitFlag(challengeName);
                    },
                    closeModal: false
                },
                {
                    text: '❌ Отмена',
                    className: 'cta-btn secondary',
                    onClick: () => {
                        console.log('🚫 Flag validation cancelled');
                    }
                }
            ]
        );


        const flagInput = modal.querySelector('#flagInput');
        flagInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                console.log('↵ Enter key pressed in flag input');
                this.submitFlag(challengeName);
            }
        });

        flagInput.focus();
        console.log('🎯 Flag input focused');
    }

    async submitFlag(challengeName) {
        console.log('🚀 Starting flag validation for:', challengeName);

        const flagInput = document.querySelector('#flagInput');
        const flagMessage = document.querySelector('#flagMessage');

        if (!flagInput || !flagMessage) {
            console.error('❌ Flag input or message element not found');
            return;
        }

        const flag = flagInput.value.trim();
        console.log('📝 Flag submitted:', flag);

        if (!flag) {
            console.warn('⚠️ Empty flag submitted');
            flagMessage.innerHTML = '<span style="color: var(--error-color);">⚠️ Введите флаг</span>';
            return;
        }

        try {
            const url = `/api/challenges/validate`;

            console.log('🌐 Making request to:', url);
            console.log('📦 Request payload:', { challengeName, flag });

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    challengeName: challengeName,
                    flag: flag
                })
            });

            console.log('📨 Response status:', response.status);

            const result = await response.json();
            console.log('📊 Response result:', result);

            if (result.success) {
                console.log('🎉 Flag validation SUCCESS');
                flagMessage.innerHTML = `<span style="color: var(--primary-color);">🎉 ${result.message}</span>`;
                this.markChallengeAsSolved(challengeName);

                setTimeout(() => {
                    const modal = document.querySelector('.challenge-modal');
                    if (modal) modal.remove();
                }, 2000);
            } else {
                console.log('❌ Flag validation FAILED:', result.message);
                flagMessage.innerHTML = `<span style="color: var(--error-color);">❌ ${result.message}</span>`;
            }
        } catch (error) {
            console.error('💥 Flag validation error:', error);
            flagMessage.innerHTML = `
                <span style="color: var(--error-color);">
                    ⚠️ Ошибка проверки флага: ${error.message}
                </span>
            `;
        }
    }


    async showHint(challengeName) {
        console.log('💡 Loading hint for:', challengeName);

        try {
            const endpoint = this.getChallengeEndpoint(challengeName);
            const url = `/challenges/${endpoint}/hint`;

            console.log('🌐 Fetching hint from:', url);

            const response = await fetch(url);
            console.log('📨 Hint response status:', response.status);

            const result = await response.json();
            console.log('💡 Hint received:', result);

            this.createChallengeModal(
                '💡 Подсказка',
                `
                    <div style="text-align: center;">
                        <div style="font-size: 3rem; margin-bottom: 1rem;">💡</div>
                        <p style="color: var(--text-secondary); line-height: 1.6;">
                            ${result.hint || 'Подсказка не найдена'}
                        </p>
                        <div style="margin-top: 2rem; padding: 1rem; background: rgba(0, 255, 136, 0.1); border-radius: 8px;">
                            <small style="color: var(--text-secondary);">
                                ⚠️ Использование подсказки может повлиять на получение очков
                            </small>
                        </div>
                    </div>
                `,
                [
                    {
                        text: 'Понятно',
                        className: 'cta-btn primary',
                        onClick: () => {
                            console.log('✅ Hint acknowledged');
                        }
                    }
                ]
            );
        } catch (error) {
            console.error('❌ Hint loading error:', error);
            CTFPlatform.showNotification('Ошибка загрузки подсказки', 'error');
        }
    }

    getChallengeEndpoint(challengeName) {
        const endpoints = {
            'SQL Injection Basic': 'sqli',
            'Authentication Bypass': 'auth-bypass',
            'XSS Challenge': 'xss',
            'CSRF Challenge': 'csrf',
            'Path Traversal': 'path-traversal'
        };

        const endpoint = endpoints[challengeName] || challengeName.toLowerCase().replace(' ', '-');
        console.log('🔗 Challenge endpoint mapping:', challengeName, '→', endpoint);

        return endpoint;
    }

    markChallengeAsSolved(challengeName) {
        console.log('🏆 Marking challenge as solved:', challengeName);

        const solvedChallenges = JSON.parse(localStorage.getItem('solvedChallenges') || '{}');
        solvedChallenges[challengeName] = true;
        localStorage.setItem('solvedChallenges', JSON.stringify(solvedChallenges));

        console.log('💾 Saved to localStorage:', solvedChallenges);


        this.updateChallengeProgress();

        CTFPlatform.showNotification(`🎉 Задание "${challengeName}" выполнено!`, 'success');
    }

    loadChallengeProgress() {
        console.log('📊 Loading challenge progress from localStorage');

        const solvedChallenges = JSON.parse(localStorage.getItem('solvedChallenges') || '{}');
        console.log('📋 Solved challenges:', solvedChallenges);


        document.querySelectorAll('.challenge-card').forEach(card => {
            const challengeName = card.querySelector('h3').textContent;
            if (solvedChallenges[challengeName]) {
                console.log('✅ Challenge already solved:', challengeName);
                card.classList.add('solved');
                const solvedBadge = document.createElement('span');
                solvedBadge.className = 'solved-badge';
                solvedBadge.textContent = '✅ Решено';
                card.appendChild(solvedBadge);
            }
        });
    }

    updateChallengeProgress() {
        console.log('🔄 Updating challenge progress UI');
    }
}

function showChallengeHint(challengeName) {
    console.log('🔧 Global showChallengeHint called for:', challengeName);

    if (window.webChallengesManager) {
        window.webChallengesManager.showHint(challengeName);
    } else {
        console.warn('⚠️ WebChallengesManager not initialized, creating fallback');
        const manager = new WebChallengesManager();
        manager.showHint(challengeName);
    }
}

function validateChallengeFlag(challengeName) {
    console.log('🔧 Global validateChallengeFlag called for:', challengeName);

    if (window.webChallengesManager) {
        window.webChallengesManager.showFlagValidationModal(challengeName);
    } else {
        console.warn('⚠️ WebChallengesManager not initialized, creating fallback');
        const manager = new WebChallengesManager();
        manager.showFlagValidationModal(challengeName);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    console.log('📄 DOM Content Loaded');

    if (window.location.pathname.includes('/category/web') ||
        window.location.pathname.includes('/challenges/')) {
        console.log('🎯 Initializing WebChallengesManager for web challenges page');
        window.webChallengesManager = new WebChallengesManager();
    } else {
        console.log('ℹ️ Not a web challenges page, skipping WebChallengesManager initialization');
    }
});


window.debugChallenges = function() {
    console.log('🐛 DEBUG CHALLENGES');
    console.log('Current URL:', window.location.href);
    console.log('WebChallengesManager:', window.webChallengesManager);
    console.log('LocalStorage solved:', JSON.parse(localStorage.getItem('solvedChallenges') || '{}'));
};