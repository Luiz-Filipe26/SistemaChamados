class PasswordEye extends HTMLElement {
  constructor() {
    super();
    this.attachShadow({ mode: 'open' });
    this.render();
    this.componentLogic();
  }

  render() {
    this.shadowRoot.innerHTML = `
      <style>
        .toggle-password {
          cursor: pointer;
        }
      </style>
      <img id="togglePassword" class="toggle-password" src="/assets/images/eye.svg" alt="Mostrar senha">
    `;
  }

  componentLogic() {
    this.isOpen = false;
    this.toggleButton = this.shadowRoot.getElementById('togglePassword');
    
    const passwordFieldId = this.getAttribute('data-password-field-id');
    
    if (passwordFieldId) {
      this.passwordId = passwordFieldId;
    }

    if (!this.toggleButton || !this.passwordId) {
      return;
    }

    this.toggleButton.addEventListener('click', this.toggleVisibility.bind(this));
  }

  toggleVisibility() {
    if (!this.passwordInput) {
      this.passwordInput = document.getElementById(this.passwordId);
    }

    if (!this.passwordInput) {
      return;
    }

    if (this.isOpen) {
      this.toggleButton.setAttribute("src", "/assets/images/eye.svg");
      this.passwordInput.setAttribute("type", "password");
    } else {
      this.toggleButton.setAttribute("src", "/assets/images/eye-off.svg");
      this.passwordInput.setAttribute("type", "text");
    }
    this.isOpen = !this.isOpen;
  }
}

customElements.define('password-eye', PasswordEye);
