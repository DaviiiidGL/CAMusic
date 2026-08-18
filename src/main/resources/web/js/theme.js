// Estado del tema guardado en memoria (no persistente por sesión)
let currentTheme = 'camel';

function applyTheme(themeName) {
    // 1. Actualizar estado
    currentTheme = themeName;

    // 2. Modificar el atributo en el DOM
    document.documentElement.setAttribute('data-theme', themeName);

    // 3. Actualizar el texto del botón
    const toggleBtn = document.getElementById('toggle-theme-btn');
    if (toggleBtn) {
        if (themeName === 'camel') {
            toggleBtn.innerText = '⛏️ Modo Enano';
        } else {
            toggleBtn.innerText = '🐪 Modo Camello';
        }
    }
}

function toggleTheme() {
    // Alternar entre los dos valores
    const newTheme = currentTheme === 'camel' ? 'dwarf' : 'camel';
    applyTheme(newTheme);
}

// Asegurarse de que el tema inicial se aplique al cargar
document.addEventListener('DOMContentLoaded', () => {
    applyTheme(currentTheme);
});