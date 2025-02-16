class NavBar extends HTMLElement {
    constructor() {
        super();
    }

    connectedCallback() {
        this.render();
    }


    render() {
        this.innerHTML = `
      <style>
        .navbar {
            background-color: #0066CC;
            padding: 10px;
            text-align: center;
        }

        .navbar ul {
            list-style-type: none;
            margin: 0;
            padding: 0;
        }

        .navbar li {
            display: inline;
            margin-right: 20px;
        }

        .navbar a {
            color: white;
            text-decoration: none;
            font-size: 18px;
            padding: 8px 16px;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .navbar a:hover {
            background-color: #555;
        }

        .navbar h1 {
            color: white;
            font-size: 20px;
            margin-top: 15px;
            margin-bottom: 5px;
        }

      </style>

        <header class="navbar">
            <ul>
                <li><a href="/tickets">Chamados</a></li>
                <li><a href="/createTicket">Criar chamado</a></li>
                <li><a href="/register">Registrar-se</a></li>
                <li><a href="/login">Fazer login</a></li>
            </ul>
            <h1>Sistema de Chamados</h1>
        </header>
    `;
    }

}

customElements.define('my-navbar', NavBar);
