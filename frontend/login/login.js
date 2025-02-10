import RequestConfig from "assets/scripts/RequestConfig";

function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (username && password) {
        fetch('http://localhost:8080/users/login',
            new RequestConfig()
            .post()
            .asJson()
            .body({
                login: username,
                password: password,
            }))
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
                alert('Login bem-sucedido!');
                window.location.href = '/tickets';
            })
            .catch(error => {
                alert('Erro na conexão. Tente novamente mais tarde.');
            });
    } else {
        alert('Por favor, preencha ambos os campos!');
    }
}

document.getElementById('signup-btn').addEventListener('click', function () {
    window.location.href = '/signup';
});