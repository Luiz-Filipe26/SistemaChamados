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
    messages.reverse();

    const newMessagesContainer = document.createElement('div');
    newMessagesContainer.id = 'messages-container';

    const newMessageButton = document.createElement('button');
    newMessageButton.classList.add('new-message-button');
    newMessageButton.id = 'new-message-button';
    newMessageButton.textContent = 'Nova Mensagem';
    newMessageButton.onclick = showNewMessageForm;

    newMessagesContainer.appendChild(newMessageButton);

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

    newMessagesContainer.append(...messagesBlock);

    return newMessagesContainer;
}

function showNewMessageForm() {
    const newMessageForm = document.createElement('div');
    newMessageForm.classList.add('new-message-form');
    newMessageForm.id = 'new-message-form';
    newMessageForm.innerHTML = `
        <textarea placeholder="Digite sua mensagem..."></textarea>
        <select>
            <option value="Pendente">Pendente</option>
            <option value="Resolvido">Resolvido</option>
            <option value="Em andamento">Em andamento</option>
        </select>
        <button>Enviar</button>
    `;
    newMessageForm.querySelector('button').onclick = sendMessage;

    const messagesContainer = document.getElementById('messages-container');
    messagesContainer.prepend(newMessageForm);

    const newMessageButton = document.getElementById('new-message-button');
    newMessageButton.classList.add('hidden');
    newMessageButton.onclick = null;
}

// Função para enviar a nova mensagem
async function sendMessage() {
    const newMessageForm = document.getElementById('new-message-form');
    const text = newMessageForm.querySelector('textarea').value;
    const status = newMessageForm.querySelector('select').value;
    const userName = localStorage.getItem('username');

    if (!text || !status || !userName) {
        alert('Preencha todos os campos.');
        return;
    }

    const response = await fetch(`http://localhost:8080/messages`,
        new RequestConfig()
            .post()
            .withAuth()
            .asJson()
            .jsonBody({
                ticketId: Number(ticketId),
                userName,
                text,
                status,
            }));

    if (!response.ok) {
        alert('Erro ao enviar mensagem.');
        return;
    }

    messagesContainer = await loadMessages();
    switchTab('messages')
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
document.getElementById('tab-description').onclick = () => switchTab('description');
document.getElementById('tab-messages').onclick = () => switchTab('messages');


switchTab('description');  