import RequestConfig from "/assets/scripts/RequestConfig.js";

window.handleLogin = handleLogin;

function handleLogin(event) {
    event.preventDefault();

    /**@type {string?} */ const username = document.getElementById('username')?.value;
    /**@type {string?} */ const password = document.getElementById('password')?.value;

    if (!username || !password) {
        alert('Por favor, preencha ambos os campos!');
        return;
    }

    fetch('http://localhost:8080/users/login',
        new RequestConfig()
            .post()
            .asJson()
            .jsonBody({
                login: username,
                password: password
            })
    )
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                alert('Credenciais inválidas. Tente novamente.');
                throw new Error('Credenciais inválidas');
            }
        })
        .then(token => {
            const jwtToken = token.replace('Bearer ', '');
            localStorage.setItem('jwtToken', jwtToken);
            localStorage.setItem('username', username);
            alert('Login bem-sucedido!');
            window.location.href = '/tickets';
        })
        .catch(error => {
            alert('Erro na conexão. Tente novamente mais tarde.');
        });
}

document.getElementById('signup-btn').addEventListener('click', function () {
    window.location.href = '/signup';
});