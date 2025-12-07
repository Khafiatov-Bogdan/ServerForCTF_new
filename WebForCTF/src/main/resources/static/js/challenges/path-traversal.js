/**
 * CTF Platform - Path Traversal Challenge
 * Логика для Path Traversal челленджа
 */

class PathTraversalChallenge {
    constructor() {
        this.basePath = '/public/';
        this.virtualFilesystem = this.createVirtualFilesystem();
        this.init();
    }

    init() {
        this.initEventListeners();
        this.initExampleButtons();
        this.loadInitialDirectory();
    }

    initEventListeners() {
        const loadBtn = document.getElementById('loadFileBtn');
        const fileInput = document.getElementById('filePath');
        const resetBtn = document.getElementById('resetBrowserBtn');
        const showExamplesBtn = document.getElementById('showAllExamplesBtn');

        if (loadBtn) {
            loadBtn.addEventListener('click', () => {
                this.loadFile();
            });
        }

        if (fileInput) {
            fileInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.loadFile();
                }
            });
        }

        if (resetBtn) {
            resetBtn.addEventListener('click', () => {
                this.resetChallenge();
            });
        }

        if (showExamplesBtn) {
            showExamplesBtn.addEventListener('click', () => {
                this.demonstrateTraversal();
            });
        }
    }

    initExampleButtons() {
        const example1Btn = document.getElementById('example1Btn');
        const example2Btn = document.getElementById('example2Btn');
        const example3Btn = document.getElementById('example3Btn');
        const example4Btn = document.getElementById('example4Btn');

        if (example1Btn) {
            example1Btn.addEventListener('click', () => {
                document.getElementById('filePath').value = '../../etc/passwd';
                this.loadFile();
            });
        }

        if (example2Btn) {
            example2Btn.addEventListener('click', () => {
                document.getElementById('filePath').value = '../secret/flag.txt';
                this.loadFile();
            });
        }

        if (example3Btn) {
            example3Btn.addEventListener('click', () => {
                document.getElementById('filePath').value = '....//....//etc/hosts';
                this.loadFile();
            });
        }

        if (example4Btn) {
            example4Btn.addEventListener('click', () => {
                document.getElementById('filePath').value = '/public/../../../secret/';
                this.loadFile();
            });
        }
    }

    createVirtualFilesystem() {
        return {
            '/public/': {
                type: 'directory',
                children: ['readme.txt', 'config.json', 'images/']
            },
            '/public/readme.txt': {
                type: 'file',
                content: 'Welcome to the file server!\nThis is a public readme file.\n\nFlag is located in /secret/flag.txt'
            },
            '/public/config.json': {
                type: 'file',
                content: '{\n  "server": "CTF Platform",\n  "version": "2.0",\n  "debug": false\n}'
            },
            '/public/images/': {
                type: 'directory',
                children: ['logo.png', 'background.jpg']
            },
            '/public/images/logo.png': {
                type: 'file',
                content: 'PNG image data (simulated)'
            },
            '/public/images/background.jpg': {
                type: 'file',
                content: 'JPEG image data (simulated)'
            },
            '/secret/': {
                type: 'directory',
                children: ['flag.txt', 'credentials.db']
            },
            '/secret/flag.txt': {
                type: 'file',
                content: 'CTF{path_traversal_success_2024}'
            },
            '/secret/credentials.db': {
                type: 'file',
                content: 'admin:supersecretpassword\nuser:weakpassword'
            },
            '/etc/': {
                type: 'directory',
                children: ['passwd', 'hosts']
            },
            '/etc/passwd': {
                type: 'file',
                content: 'root:x:0:0:root:/root:/bin/bash\nadmin:x:1000:1000:admin:/home/admin:/bin/bash'
            },
            '/etc/hosts': {
                type: 'file',
                content: '127.0.0.1 localhost\n127.0.1.1 ctf-platform'
            }
        };
    }

    loadFile() {
        const fileInput = document.getElementById('filePath');
        const fileContent = document.getElementById('fileContent');
        const currentPath = document.getElementById('currentPath');

        if (!fileInput || !fileContent) return;

        const path = fileInput.value.trim();
        if (!path) {
            this.showError('Please enter a file path');
            return;
        }


        if (currentPath) {
            currentPath.textContent = path;
        }


        const normalizedPath = this.normalizePath(path);
        const file = this.virtualFilesystem[normalizedPath];

        if (!file) {
            this.showError(`File not found: ${path}`);
            return;
        }

        if (file.type === 'directory') {
            this.showDirectory(normalizedPath, file);
        } else {
            this.showFile(normalizedPath, file);
        }


        this.checkPathTraversal(path, normalizedPath);
    }

    normalizePath(path) {

        if (!path.startsWith('/')) {
            path = this.basePath + path;
        }


        let normalized = path;
        while (normalized.includes('/../')) {
            normalized = normalized.replace(/[^/]+\/\.\.\//, '');
        }


        normalized = normalized.replace(/\/+/g, '/');


        if (normalized.endsWith('/') && !this.virtualFilesystem[normalized]) {
            normalized = normalized.slice(0, -1);
        }

        return normalized;
    }

    showDirectory(path, directory) {
        const fileContent = document.getElementById('fileContent');
        fileContent.innerHTML = '';

        const header = document.createElement('h4');
        header.textContent = `📁 Directory: ${path}`;
        fileContent.appendChild(header);

        const fileList = document.createElement('ul');
        fileList.className = 'file-list';

        directory.children.forEach(child => {
            const listItem = document.createElement('li');
            listItem.className = 'file-item';

            const fullPath = path.endsWith('/') ? path + child : path + '/' + child;
            const isDir = this.virtualFilesystem[fullPath]?.type === 'directory';

            listItem.className += isDir ? ' directory' : ' file';
            listItem.innerHTML = `
                ${isDir ? '📁' : '📄'} ${child}
                ${!isDir ? '<span class="file-size">1.2KB</span>' : ''}
            `;

            listItem.addEventListener('click', () => {
                document.getElementById('filePath').value = fullPath;
                this.loadFile();
            });

            fileList.appendChild(listItem);
        });

        fileContent.appendChild(fileList);
        fileContent.className = 'file-content';


        this.hideFileInfo();
    }

    showFile(path, file) {
        const fileContent = document.getElementById('fileContent');
        fileContent.innerHTML = '';

        const header = document.createElement('h4');
        header.textContent = `📄 File: ${path}`;
        fileContent.appendChild(header);

        const contentDiv = document.createElement('div');
        contentDiv.className = 'file-content-text';
        contentDiv.textContent = file.content;


        if (path.endsWith('.json')) {
            this.highlightJSON(contentDiv);
        }

        fileContent.appendChild(contentDiv);
        fileContent.className = 'file-content success';


        this.showFileInfo(path, file);
    }

    showFileInfo(path, file) {
        const fileInfo = document.getElementById('fileInfo');
        const infoPath = document.getElementById('infoPath');
        const infoSize = document.getElementById('infoSize');
        const infoType = document.getElementById('infoType');
        const infoPermissions = document.getElementById('infoPermissions');

        if (fileInfo && infoPath && infoSize && infoType && infoPermissions) {
            infoPath.textContent = path;
            infoSize.textContent = this.calculateFileSize(file.content);
            infoType.textContent = this.getFileType(path);
            infoPermissions.textContent = this.getFilePermissions(path);

            fileInfo.style.display = 'block';
        }
    }

    hideFileInfo() {
        const fileInfo = document.getElementById('fileInfo');
        if (fileInfo) {
            fileInfo.style.display = 'none';
        }
    }

    calculateFileSize(content) {
        const bytes = new Blob([content]).size;
        if (bytes < 1024) return bytes + ' bytes';
        if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
        return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
    }

    getFileType(path) {
        if (path.endsWith('.txt')) return 'Text File';
        if (path.endsWith('.json')) return 'JSON File';
        if (path.endsWith('.png') || path.endsWith('.jpg')) return 'Image File';
        if (path.endsWith('.db')) return 'Database File';
        return 'Unknown';
    }

    getFilePermissions(path) {
        if (path.includes('/secret/') || path.includes('/etc/')) {
            return 'rw------- (600)';
        }
        return 'rw-r--r-- (644)';
    }

    showError(message) {
        const fileContent = document.getElementById('fileContent');
        fileContent.innerHTML = '';

        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.innerHTML = `❌ ${message}`;

        fileContent.appendChild(errorDiv);
        fileContent.className = 'file-content error';


        this.hideFileInfo();
    }

    highlightJSON(element) {
        try {
            const json = JSON.parse(element.textContent);
            element.textContent = JSON.stringify(json, null, 2);
        } catch (e) {

        }
    }

    checkPathTraversal(originalPath, normalizedPath) {

        const traversalPatterns = [
            /\.\.\//,
            /\/etc\//,
            /\/secret\//,
            /\/\.\.\//,
            /\/passwd/,
            /\/flag/
        ];

        const hasTraversal = traversalPatterns.some(pattern =>
            pattern.test(originalPath) || pattern.test(normalizedPath)
        );

        if (hasTraversal && normalizedPath === '/secret/flag.txt') {
            this.showNotification('🎉 Path traversal detected! Flag captured!', 'success');
            this.showFlag();
        } else if (hasTraversal) {
            this.showNotification('⚠️ Path traversal attempt detected', 'warning');
        }
    }

    showFlag() {
        const flag = 'CTF{p4th_tr4v3rs4l_w1n_2024}';
        const fileBrowser = document.querySelector('.file-browser');
        if (!fileBrowser) return;


        const existingFlag = fileBrowser.querySelector('.flag-message');
        if (existingFlag) {
            existingFlag.remove();
        }

        const flagElement = document.createElement('div');
        flagElement.className = 'flag-message';
        flagElement.innerHTML = `
            <h4>🎉 Path Traversal Successful!</h4>
            <div class="flag">${flag}</div>
            <small>Click to copy flag to clipboard</small>
        `;

        flagElement.addEventListener('click', () => {
            this.copyToClipboard(flag);
            this.showNotification('Flag copied to clipboard!', 'success');
        });

        fileBrowser.appendChild(flagElement);


        this.copyToClipboard(flag);
    }

    copyToClipboard(text) {
        navigator.clipboard.writeText(text).catch(err => {
            console.error('Failed to copy: ', err);
        });
    }

    showNotification(message, type = 'info') {

        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 12px 20px;
            background: ${type === 'success' ? '#4CAF50' : type === 'warning' ? '#FF9800' : '#2196F3'};
            color: white;
            border-radius: 4px;
            z-index: 1000;
            font-family: 'Roboto', sans-serif;
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.remove();
        }, 3000);
    }

    loadInitialDirectory() {
        const fileInput = document.getElementById('filePath');
        const currentPath = document.getElementById('currentPath');

        if (fileInput) {
            fileInput.value = this.basePath;
        }
        if (currentPath) {
            currentPath.textContent = this.basePath;
        }

        this.showDirectory(this.basePath, this.virtualFilesystem[this.basePath]);
    }


    demonstrateTraversal() {
        const examples = [
            '../../etc/passwd',
            '../secret/flag.txt',
            '....//....//....//etc/hosts',
            '/public/../../secret/credentials.db'
        ];

        console.log('Path Traversal Examples:');
        examples.forEach(example => {
            console.log(`- ${example} -> ${this.normalizePath(example)}`);
        });

        this.showNotification('Traversal examples logged to console', 'info');

        alert('Path Traversal Examples:\n\n' + examples.join('\n') + '\n\nCheck console for normalized paths');
    }

    resetChallenge() {
        const fileInput = document.getElementById('filePath');
        const currentPath = document.getElementById('currentPath');

        if (fileInput) fileInput.value = this.basePath;
        if (currentPath) currentPath.textContent = this.basePath;

        this.loadInitialDirectory();
        this.showNotification('File browser reset to initial state', 'info');


        const flagMessage = document.querySelector('.flag-message');
        if (flagMessage) {
            flagMessage.remove();
        }
    }
}


document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('/path-traversal')) {
        window.pathTraversalChallenge = new PathTraversalChallenge();
        console.log('Path Traversal Challenge initialized');
    }
});


window.loadFile = function() {
    if (window.pathTraversalChallenge) {
        window.pathTraversalChallenge.loadFile();
    }
};