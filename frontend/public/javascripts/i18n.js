/*
https://github.com/muskf/site/blob/main/i18n.js
muskf
* */
function detectUserLanguage() {
    const userLanguage = navigator.language || navigator.userLanguage || 'en';
    return userLanguage;
}

function loadLanguageResources() {
    const userLanguage = detectUserLanguage();
    let languageCode;

    if (userLanguage.startsWith('zh')) {
        languageCode = 'zh-CN';
    } else {
        languageCode = 'en';
    }

    fetch(`i18n/${languageCode}.json`)
        .then(response => response.json())
        .then(data => {
            const elements = document.querySelectorAll('[data-i18n]');
            elements.forEach(element => {
                const key = element.getAttribute('data-i18n');
                const translation = data[key];
                if (translation) {
                    element.textContent = translation;
                }
            });
        });
}

window.addEventListener('load', loadLanguageResources);