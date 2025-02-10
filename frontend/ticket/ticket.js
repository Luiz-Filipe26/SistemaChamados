import FetchInterceptor from "/assets/scripts/FetchInterceptor.js";
import RequestConfig from "/assets/scripts/RequestConfig.js";

FetchInterceptor.create(FetchInterceptor.handleUnauthorized);

const urlParams = new URLSearchParams(window.location.search);
const ticketId = urlParams.get('ticketId');

const ticketDetails = await fetch(`http://localhost:8080/tickets/${ticketId}`,
    new RequestConfig().get().withAuth().asJson())
    .then(response => {
        if (!response.ok) {
            throw new Error(`Erro ao buscar ticket: ${response.statusText}`);
        }
        return response.json();
    })
    .catch(error => {
        console.error(error);
        return null;
    });

if (ticketDetails) {
    console.log(ticketDetails);
}

const ticketDescription = ticketDetails
    ? ` <p>Chamado aberto por ${ticketDetails.userName}.</p>
        <p>Data de criação: ${ticketDetails.creationDate}.</p>
        <p>Última atualização em: ${ticketDetails.updateDate}.</p>
        <p>Status: ${ticketDetails.status}.</p>
        <br/>
        <strong>Descrição:</strong>
        <p>${ticketDetails.description}</p> `
    : '<p>Descrição não disponível.</p>';

const tabsContent = {
    description: `<p>${ticketDescription}</p>`,
    messages: `<div id="messages-container"></div>`
};

// Variável para armazenar o bloco de mensagens
let messagesContainer = await loadMessages();

// Função para carregar as mensagens em uma div
async function loadMessages() {
    const response = await fetch(`http://localhost:8080/messages/ticket/${ticketId}`,
        new RequestConfig().get().withAuth().asJson());

    if (!response.ok) {
        console.error(`Erro ao buscar mensagens: ${response.statusText}`);
        return null;
    }

    const messages = await response.json();

    const newMessagesContainer = document.createElement('div');
    newMessagesContainer.id = 'messages-container';

    const messagesBlock = messages.map(msg => {
        const messageBlock = document.createElement('div');
        messageBlock.classList.add('message-block');

        messageBlock.innerHTML = `
            <strong>Autor: ${msg.userName}:</strong>
            <p><small>Data de criação: ${msg.creationDate} às ${msg.creationTime}</small></p>
            <p><small>Status: ${msg.status}</small></p>
            <p>${msg.text}</p>
        `;

        return messageBlock;
    });

    newMessagesContainer.appendChild(...messagesBlock);

    return newMessagesContainer;
}

// Função para exibir as mensagens
async function displayMessages() {
    const tabContent = document.getElementById('tab-content');

    // Limpa qualquer conteúdo anterior e adiciona o bloco de mensagens
    tabContent.innerHTML = ''; // Limpa conteúdo existente
    tabContent.appendChild(messagesContainer);
}

function switchTab(tab) {
    const tabContent = document.getElementById('tab-content');
    tabContent.innerHTML = tabsContent[tab];

    document.querySelectorAll('.tab-button').forEach(button => button.classList.remove('active'));
    document.getElementById(`tab-${tab}`).classList.add('active');

    if (tab === 'messages') {
        displayMessages();
    }
}

document.getElementById('ticket-number').textContent = '#' + String(ticketId).padStart(6, '0') || 'Não fornecido';

document.getElementById('tab-description').addEventListener('click', () => switchTab('description'));
document.getElementById('tab-messages').addEventListener('click', () => switchTab('messages'));

switchTab('description');  