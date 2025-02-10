import RequestConfig from "/assets/scripts/RequestConfig.js";

window.handleRegister = handleRegister;
function handleRegister(event) {
    event.preventDefault();

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const birthDate = document.getElementById('birthDate').value;
    const role = document.getElementById('role').value;
    const location = document.getElementById('location').value;

    if (name && email && password && birthDate && role && location) {
        fetch('http://localhost:8080/users/register',
            new RequestConfig()
                .post()
                .asJson()
                .jsonBody({
                    name,
                    email,
                    password,
                    birthDate,
                    role,
                    location,
                })
        )
            .then(response => {
                if (response.ok) {
                    alert('Registro bem-sucedido!');
                    window.location.href = '/login';
                } else {
                    alert('Erro ao registrar. Tente novamente.');
                    throw new Error('Erro ao registrar');
                }
            })
            .catch(error => {
                alert('Erro na conexão. Tente novamente mais tarde.');
            });
    } else {
        alert('Por favor, preencha todos os campos!');
    }
}