class CTFLeaderboard {
    constructor() {
        this.currentView = 'top3';
        this.init();
    }

    init() {
        this.initEventListeners();
        this.renderLeaderboard();
    }

    initEventListeners() {
        const toggleBtn = document.getElementById('toggleLeaderboard');
        if (toggleBtn) toggleBtn.addEventListener('click', () => this.toggleView());
    }

    toggleView() {
        this.currentView = this.currentView === 'top3' ? 'full' : 'top3';
        this.renderLeaderboard();
        this.updateToggleButton();
    }

    updateToggleButton() {
        const toggleBtn = document.getElementById('toggleLeaderboard');
        const header = document.querySelector('.leaderboard-header h3');
        const widget = document.querySelector('.leaderboard-widget');

        if (!toggleBtn || !header || !widget) return;

        if (this.currentView === 'top3') {
            toggleBtn.textContent = 'Показать всех участников';
            header.textContent = '🏆 ТОП 3';
            widget.classList.remove('expanded');
        } else {
            toggleBtn.textContent = 'Показать ТОП 3';
            header.textContent = '🏆 Полный список';
            widget.classList.add('expanded');
        }
    }

    renderLeaderboard() {
        if (this.currentView === 'top3') {
            document.getElementById('leaderboardFull')?.style.setProperty('display', 'none');
            document.getElementById('leaderboardTop3')?.style.setProperty('display', 'flex');
            generateTop3Leaderboard();
        } else {
            document.getElementById('leaderboardTop3')?.style.setProperty('display', 'none');
            document.getElementById('leaderboardFull')?.style.setProperty('display', 'flex');
            generateFullLeaderboard();
        }
    }
}

// ===== ТОП 3 =====
function generateTop3Leaderboard() {
    const top3List = document.getElementById('leaderboardTop3');
    if (!top3List) return;

    top3List.innerHTML = '';

    fetch('http://localhost:8081/top3', { credentials: 'include' })
        .then(res => res.json())
        .then(users => {
            if (!Array.isArray(users) || users.length === 0) {
                top3List.innerHTML = '<div class="no-users-message">Нет данных о пользователях</div>';
                return;
            }

            users.slice(0, 3).forEach((user, index) => {
                const login = user.login ?? 'Unknown';
                const points = user.points ?? 0;
                const labPoints = user.pointsLab ?? 0;

                const leaderItem = document.createElement('div');
                leaderItem.className = 'leader-item';
                leaderItem.style.animationDelay = `${index * 0.2}s`;

                leaderItem.innerHTML = `
                    <div class="leader-rank">${index + 1}</div>
                    <div class="leader-info">
                        <div class="leader-name">${login}</div>
                        <div class="leader-stats">
                            Points: ${points} | Lab Points: ${labPoints}
                        </div>
                    </div>
                `;
                top3List.appendChild(leaderItem);
            });
        })
        .catch(err => {
            console.error('Ошибка при загрузке ТОП 3:', err);
            top3List.innerHTML = '<div class="no-users-message">Ошибка загрузки данных</div>';
        });
}

// ===== Полный список =====
function generateFullLeaderboard() {
    const fullList = document.getElementById('leaderboardFull');
    if (!fullList) return;

    fullList.innerHTML = '';

    fetch('http://localhost:8081/allNames', { credentials: 'include' })
        .then(res => res.json())
        .then(users => {
            if (!Array.isArray(users) || users.length === 0) {
                fullList.innerHTML = '<div class="no-users-message">Нет пользователей</div>';
                return;
            }

            users.forEach((user, index) => {
                const login = user.name ?? 'Unknown';
                const points = user.points ?? 0;
                const labPoints = user.pointsLab ?? 0;

                // Karlapingus получает tooltip
                const nameHtml = login === 'Karlapingus'
                    ? `<div class="leader-name tooltip" data-tip="ПодарОчек: MINUS200">${login}</div>`
                    : `<div class="leader-name">${login}</div>`;

                const leaderItem = document.createElement('div');
                leaderItem.className = 'leader-item';
                leaderItem.style.animationDelay = `${index * 0.1}s`;

                leaderItem.innerHTML = `
                    <div class="leader-rank">${index + 1}</div>
                    <div class="leader-info">
                        ${nameHtml}
                        <div class="leader-stats">
                            Points: ${points} | Lab Points: ${labPoints}
                        </div>
                    </div>
                `;

                fullList.appendChild(leaderItem);
            });

        })
        .catch(err => {
            console.error('Ошибка при загрузке полного списка:', err);
            fullList.innerHTML = '<div class="no-users-message">Ошибка загрузки данных</div>';
        });
}


// Инициализация
document.addEventListener('DOMContentLoaded', () => {
    if (document.querySelector('.leaderboard-widget')) {
        new CTFLeaderboard();
    }
});
