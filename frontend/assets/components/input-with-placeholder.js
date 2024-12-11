class InputWithPlaceholder extends HTMLElement {
  constructor() {
    super();
  }

  connectedCallback() {
    this.render();
    this.componentLogic();
  }

  generateSixDigitsRandomId() {
    return Math.floor(100000 + Math.random() * 900000);
  }

  render() {
    const randomId = this.generateSixDigitsRandomId();

    this.innerHTML = `
      <style>
        .input-container {
          position: relative;
          display: block;
          margin: 0;
        }
        .input-field {
          padding: 12px 10px;
          font-size: 14px;
          border: 1px solid #ccc;
          border-radius: 4px;
        }
        .input-label {
          font-size: 14px;
          color: rgb(100, 100, 100);
          position: absolute;
          left: 10px;
          top: 10px;
          pointer-events: none;
          transition: all 0.3s ease;
        }
        .input-field:focus + .input-label,
        .input-field:not(:placeholder-shown) + .input-label {
          top: -18px;
          font-size: 15px;
          color: rgb(136, 136, 136);
        }
      </style>
      <div class="input-container">
        <input class="input-field" id="inputField${randomId}" placeholder="" />
        <label class="input-label" for="inputField${randomId}"></label>
      </div>
    `;
  }

  componentLogic() {
    this.inputField = this.querySelector('.input-field');
    this.inputLabel = this.querySelector('.input-label');

    const placeholderText = this.getAttribute('data-placeholder') || 'Digite algo...';
    const inputType = this.getAttribute('data-type') || 'text';
    const inputWidth = this.getAttribute('data-width') || '200px';
    
    this.inputField.setAttribute('type', inputType);
    this.inputField.style.width = inputWidth;
    this.inputLabel.innerText = placeholderText;

    if (inputType === 'date') {    
      const checkDate = () => {
        const currentValue = this.inputField.value;
    
        if (!currentValue || currentValue === '') {
          this.inputField.style.setProperty('color', 'rgb(100, 100, 100)');
        } else {
          this.inputField.style.setProperty('color', 'black');
        }
      };

      checkDate();
    
      this.inputField.addEventListener("change", checkDate);
    }
  }
}

customElements.define('input-with-placeholder', InputWithPlaceholder);
