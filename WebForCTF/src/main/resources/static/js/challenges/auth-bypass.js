/**
 * CTF Platform - Auth Bypass Challenge
 * Логика для обхода аутентификации
 */

class AuthBypassChallenge {
    constructor() {
        this.accessLog = [];
        this.isAdmin = false;
        this.init();
    }

    init() {
        this.initEventListeners();
        this.loadAccessLog();
    }

    initEventListeners() {

        document.getElementById('checkAccessBtn')?.addEventListener('click', () => this.checkAccess());
        document.getElementById('resetAccessBtn')?.addEventListener('click', () => this.resetChallenge());


        document.getElementById('cookieMethodBtn')?.addEventListener('click', () => exploitCookie());
        document.getElementById('localStorageBtn')?.addEventListener('click', () => exploitLocalStorage());
        document.getElementById('sessionStorageBtn')?.addEventListener('click', () => exploitSessionStorage());
        document.getElementById('urlMethodBtn')?.addEventListener('click', () => exploitURLParams());


        const checkAccessBtn = document.querySelector('button[onclick="checkAccess()"]');
        if (checkAccessBtn) {
            checkAccessBtn.addEventListener('click', () => this.checkAccess());
        }
    }

    checkAccess() {

        const cookies = document.cookie;
        const isAdminCookie = this.getCookie('isAdmin');


        if (isAdminCookie === 'true' || this.checkURLParams() || this.checkLocalStorage()) {
            this.grantAdminAccess();
        } else {
            this.denyAccess();
        }

        this.logAccessAttempt(isAdminCookie === 'true');
    }

    getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

    checkURLParams() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('admin') === 'true' || urlParams.get('debug') === '1';
    }

    checkLocalStorage() {
        return localStorage.getItem('admin') === 'true' ||
               sessionStorage.getItem('privileges') === 'admin';
    }

    grantAdminAccess() {
        this.isAdmin = true;
        this.showAdminContent();
        this.logSuccessfulBypass();
        if (window.CTFPlatform && window.CTFPlatform.showNotification) {
            window.CTFPlatform.showNotification('Admin access granted!', 'success');
        }
    }

    denyAccess() {
        this.isAdmin = false;
        this.hideAdminContent();
        if (window.CTFPlatform && window.CTFPlatform.showNotification) {
            window.CTFPlatform.showNotification('Access denied. Admin privileges required.', 'error');
        }
    }

    showAdminContent() {
        const adminContent = document.getElementById('adminContent');
        if (adminContent) {
            adminContent.style.display = 'block';


            if (this.detectBypassMethod()) {
                this.showFlag();
            }
        }
    }

    hideAdminContent() {
        const adminContent = document.getElementById('adminContent');
        if (adminContent) {
            adminContent.style.display = 'none';
        }
    }

    detectBypassMethod() {
        const urlParams = new URLSearchParams(window.location.search);
        const cookies = document.cookie;


        if (urlParams.get('admin') === 'true' ||
            urlParams.get('debug') === '1' ||
            cookies.includes('isAdmin=true') ||
            localStorage.getItem('admin') === 'true' ||
            sessionStorage.getItem('privileges') === 'admin') {
            return true;
        }
        return false;
    }

    showFlag() {
        const flag = 'CTF{4uth_byp455_3xp3rt_2024}';
        const adminContent = document.getElementById('adminContent');
        if (!adminContent) return;

        const flagElement = document.createElement('div');
        flagElement.className = 'message success';
        flagElement.innerHTML = `
            🎉 Authentication Bypass Successful!<br>
            <strong>Flag: ${flag}</strong><br>
            <small>Vulnerability: Client-side authentication check</small>
        `;

        flagElement.addEventListener('click', () => {
            if (window.CTFUtils && window.CTFUtils.copyToClipboard) {
                window.CTFUtils.copyToClipboard(flag);
            }
        });

        adminContent.appendChild(flagElement);

        if (window.CTFUtils && window.CTFUtils.copyToClipboard) {
            window.CTFUtils.copyToClipboard(flag);
        }
    }

    logAccessAttempt(success) {
        const timestamp = new Date();
        const method = this.detectBypassMethod() ? 'BYPASS' : 'NORMAL';
        const userAgent = navigator.userAgent;

        this.accessLog.unshift({
            timestamp: timestamp,
            success: success,
            method: method,
            userAgent: userAgent.substring(0, 50) + '...'
        });

        this.updateAccessLog();
    }

    logSuccessfulBypass() {
        console.log('Auth bypass methods:');
        console.log('- URL Parameters:', this.checkURLParams());
        console.log('- Cookies:', this.getCookie('isAdmin'));
        console.log('- LocalStorage:', localStorage.getItem('admin'));
        console.log('- SessionStorage:', sessionStorage.getItem('privileges'));
    }

    updateAccessLog() {
        const logEntries = document.querySelector('.log-entries');
        if (!logEntries) return;

        logEntries.innerHTML = '';

        this.accessLog.slice(0, 10).forEach(entry => {
            const logEntry = this.createLogEntry(entry);
            logEntries.appendChild(logEntry);
        });
    }

    createLogEntry(entry) {
        const logElement = document.createElement('div');
        logElement.className = `log-entry ${entry.success ? 'success' : 'failed'}`;

        const time = entry.timestamp.toLocaleTimeString('ru-RU');
        logElement.textContent = `[${time}] ${entry.method} - ${entry.success ? 'GRANTED' : 'DENIED'}`;

        return logElement;
    }

    loadAccessLog() {

        this.accessLog = [
            {
                timestamp: new Date(Date.now() - 300000),
                success: false,
                method: 'NORMAL',
                userAgent: 'Mozilla/5.0...'
            },
            {
                timestamp: new Date(Date.now() - 600000),
                success: false,
                method: 'NORMAL',
                userAgent: 'Mozilla/5.0...'
            }
        ];
        this.updateAccessLog();
    }


    setAdminCookie() {
        document.cookie = "isAdmin=true; path=/; max-age=3600";
        if (window.CTFPlatform && window.CTFPlatform.showNotification) {
            window.CTFPlatform.showNotification('Admin cookie set. Try accessing admin panel.', 'info');
        }
    }

    setAdminLocalStorage() {
        localStorage.setItem('admin', 'true');
        if (window.CTFPlatform && window.CTFPlatform.showNotification) {
            window.CTFPlatform.showNotification('Admin flag set in localStorage.', 'info');
        }
    }

    setAdminSessionStorage() {
        sessionStorage.setItem('privileges', 'admin');
        if (window.CTFPlatform && window.CTFPlatform.showNotification) {
            window.CTFPlatform.showNotification('Admin privileges set in sessionStorage.', 'info');
        }
    }

    resetChallenge() {
        this.isAdmin = false;
        this.accessLog = [];
        this.hideAdminContent();
        this.loadAccessLog();


        document.cookie = "isAdmin=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        localStorage.removeItem('admin');
        sessionStorage.removeItem('privileges');


        const url = new URL(window.location);
        url.searchParams.delete('admin');
        url.searchParams.delete('debug');
        window.history.replaceState({}, '', url);

        if (window.CTFPlatform && window.CTFPlatform.showNotification) {
            window.CTFPlatform.showNotification('Challenge reset. All bypass methods cleared.', 'info');
        }
    }
}


function checkAccess() {
    if (window.authBypassChallenge) {
        window.authBypassChallenge.checkAccess();
    }
}


function exploitCookie() {
    if (window.authBypassChallenge) {
        window.authBypassChallenge.setAdminCookie();
    }
}

function exploitLocalStorage() {
    if (window.authBypassChallenge) {
        window.authBypassChallenge.setAdminLocalStorage();
    }
}

function exploitSessionStorage() {
    if (window.authBypassChallenge) {
        window.authBypassChallenge.setAdminSessionStorage();
    }
}

function exploitURLParams() {
    const url = new URL(window.location);
    url.searchParams.set('admin', 'true');
    window.history.replaceState({}, '', url);

    if (window.CTFPlatform && window.CTFPlatform.showNotification) {
        window.CTFPlatform.showNotification('URL parameters set. Try checking access now.', 'info');
    }


    setTimeout(() => {
        if (window.authBypassChallenge) {
            window.authBypassChallenge.checkAccess();
        }
    }, 500);
}


document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('/auth-bypass')) {
        window.authBypassChallenge = new AuthBypassChallenge();
    }
});