function loadComponents() {
  const componentTags = new Set(
    Array.from(document.querySelectorAll('*'))
      .filter(component => component.tagName.includes('-'))
      .map(component => `./components/${component.tagName.toLowerCase()}.js`)
  );

  componentTags.forEach(loadScript);
}

function loadScript(scriptUrl) {
  const script = document.createElement('script');
  script.src = scriptUrl;
//   script.onload = () => {
//     console.log(`Script carregado: ${scriptUrl}`);
//   };
//   script.onerror = () => {
//     console.error(`Erro ao carregar o script: ${scriptUrl}`);
//   };

  document.head.appendChild(script);
}

document.addEventListener('DOMContentLoaded', loadComponents);
