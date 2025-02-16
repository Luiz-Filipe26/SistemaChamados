import RequestConfig from '/assets/scripts/RequestConfig.js';
import FetchInterceptor from '/assets/scripts/FetchInterceptor.js';

FetchInterceptor.create(FetchInterceptor.handleUnauthorized);

document.getElementById('ticketForm').onsubmit = onSubmitTicketForm;

function onSubmitTicketForm(e) {
    e.preventDefault();

    const title = document.getElementById('title').value.trim();
    const description = document.getElementById('description').value.trim();

    if (!title || !description) {
        alert('Todos os campos devem ser preenchidos.');
        return;
    }

    const userName = localStorage.getItem('username');
    if (!userName) {
        alert('Erro: usuário não encontrado. Faça login novamente.');
        return;
    }

    fetch(
        'http://localhost:8080/tickets',
        new RequestConfig().post().withAuth().jsonBody({
            title,
            description,
            userName
        })
    )
        .then(response => {
            if (!response.ok) throw new Error('Erro ao criar chamado');
            alert('Chamado criado com sucesso!');
            window.location.href = '/tickets';
        })
        .catch(error => {
            console.error(error);
            alert('Erro ao criar chamado');
        });
}