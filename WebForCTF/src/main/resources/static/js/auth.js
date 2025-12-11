/**
 * CTF Platform - Login Logic
 * Только логин и OAuth
 */

class CTFAuth {
    constructor() {
        this.init();
    }

    init() {
        this.initFormValidation();
        this.initRealTimeValidation();
        this.initPasswordStrength();
        this.initOAuthHandlers();
        this.initButtonAnimations();
    }

    initFormValidation() {
        const form = document.querySelector('.auth-form'); // только login
        if (!form) return;

        const submitBtn = form.querySelector('button[type="submit"]');

        form.addEventListener('submit', (e) => {
            if (!this.validateForm(form)) {
                e.preventDefault();
                return;
            }

            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = `<span class="btn-icon">⏳</span> Вход...`;
            }
        });
    }

    initRealTimeValidation() {
        const usernameInput = document.querySelector('input[name="username"]');
        const passwordInput = document.querySelector('input[name="password"]');

        if (usernameInput) {
            usernameInput.addEventListener('input', (e) => {
                this.clearValidationMessage(usernameInput);
            });
        }

        if (passwordInput) {
            passwordInput.addEventListener('input', () => {
                this.clearValidationMessage(passwordInput);
            });
        }
    }

    initPasswordStrength() {
        const passwordInput = document.querySelector('input[name="password"]');
        if (!passwordInput) return;

        passwordInput.addEventListener('input', (e) => {
            this.updatePasswordStrength(e.target.value);
        });
    }

    initOAuthHandlers() {
        const oauthButtons = document.querySelectorAll('.social-btn, .oauth-btn');
        oauthButtons.forEach(btn => {
            btn.addEventListener('mouseenter', this.addRippleEffect.bind(this));
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const provider = btn.textContent.trim();
                this.showOAuthMessage(provider);
            });
        });
    }

    initButtonAnimations() {
        document.querySelectorAll('.auth-btn, .social-btn').forEach(btn => {
            btn.addEventListener('mouseenter', () => btn.style.transform = 'translateY(-2px)');
            btn.addEventListener('mouseleave', () => btn.style.transform = 'translateY(0)');
            btn.addEventListener('click', () => {
                btn.style.transform = 'translateY(1px)';
                setTimeout(() => btn.style.transform = 'translateY(-2px)', 150);
            });
        });
    }

    validateForm(form) {
        const username = form.querySelector('input[name="username"]');
        const password = form.querySelector('input[name="password"]');

        let isValid = true;

        if (!username || !username.value.trim()) {
            this.showValidationMessage(username, 'Введите имя пользователя');
            isValid = false;
        }

        if (!password || password.value.length < 6) {
            this.showValidationMessage(password, 'Пароль должен быть не менее 6 символов');
            isValid = false;
        }

        return isValid;
    }

    updatePasswordStrength(password) {
        const strengthElement = document.getElementById('passwordStrength');
        if (!strengthElement) return;

        let strength = 0;
        if (password.length >= 6) strength++;
        if (password.length >= 8) strength++;
        if (/[A-Z]/.test(password)) strength++;
        if (/[0-9]/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        let message = '', color = '';
        switch (strength) {
            case 0: case 1: message = 'Очень слабый'; color = 'red'; break;
            case 2: message = 'Слабый'; color = '#ff4444'; break;
            case 3: message = 'Средний'; color = '#ffa500'; break;
            case 4: message = 'Сильный'; color = '#00ff88'; break;
            case 5: message = 'Очень сильный'; color = 'var(--primary-color)'; break;
        }

        strengthElement.textContent = `Сила пароля: ${message}`;
        strengthElement.style.color = color;
        strengthElement.style.display = password ? 'block' : 'none';
    }

    showValidationMessage(inputElement, message) {
        if (!inputElement) return;

        let validationElement = inputElement.parentNode.querySelector('.validation-message');
        if (!validationElement) {
            validationElement = document.createElement('div');
            validationElement.className = 'validation-message invalid';
            inputElement.parentNode.appendChild(validationElement);
        }

        validationElement.textContent = message;
        validationElement.style.display = 'block';
    }

    clearValidationMessage(inputElement) {
        if (!inputElement) return;

        const validationElement = inputElement.parentNode.querySelector('.validation-message');
        if (validationElement) {
            validationElement.style.display = 'none';
        }
    }

    addRippleEffect(e) {
        const btn = e.currentTarget;
        const ripple = document.createElement('span');
        const rect = btn.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        const x = e.clientX - rect.left - size / 2;
        const y = e.clientY - rect.top - size / 2;

        ripple.style.cssText = `
            position: absolute;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.3);
            transform: scale(0);
            animation: ripple 0.6s linear;
            width: ${size}px;
            height: ${size}px;
            left: ${x}px;
            top: ${y}px;
            pointer-events: none;
        `;

        if (!document.querySelector('#ripple-styles')) {
            const style = document.createElement('style');
            style.id = 'ripple-styles';
            style.textContent = `
                @keyframes ripple {
                    to {
                        transform: scale(4);
                        opacity: 0;
                    }
                }
            `;
            document.head.appendChild(style);
        }

        btn.appendChild(ripple);
        setTimeout(() => ripple.remove(), 600);
    }

    showOAuthMessage(provider) {
        this.createOAuthNotification(provider);
    }

    createOAuthNotification(provider) {
        // оставляем реализацию твою без изменений
    }
}


document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('/auth')) {
        window.ctfAuth = new CTFAuth();
    }
});
