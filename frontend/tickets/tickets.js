import RequestConfig from '/assets/scripts/RequestConfig.js';
import FetchInterceptor from '/assets/scripts/FetchInterceptor.js';

FetchInterceptor.create(FetchInterceptor.handleUnauthorized);
let ticketsData = []; // Armazena os tickets recuperados globalmente

async function getTickets(filters) {
    const endpoint = 'http://localhost:8080/tickets';

    try {

        const requestConfig = new RequestConfig()
            .get()
            .withAuth()
            .asJson();

        const response = await fetch(endpoint, requestConfig);

        if (!response.ok) {
            throw new Error(`Erro ao buscar tickets: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(error);
        return [];
    }
}

function createRow(tableBody, ticket) {
    const row = document.createElement('tr');

    row.innerHTML = `
        <td>${ticket.id}</td>
        <td>${ticket.title}</td>
        <td>${ticket.status}</td>
        <td>${ticket.creationDate}</td>
        <td>${ticket.updateDate}</td>
    `;

    row.addEventListener('click', () => {
        const selectedTicket = ticketsData.find(t => t.number === ticket.number);
        if (selectedTicket) {
            const targetUrl = `/ticket/?ticketId=${selectedTicket.id}`;
            window.open(targetUrl, '_blank');
        }
    });

    tableBody.appendChild(row);
}

async function populateTicketsTable(filters) {
    const tableBody = document.getElementById('ticketsTableBody');
    tableBody.innerHTML = '';

    try {
        ticketsData = await getTickets(filters);

        ticketsData.forEach(ticket => createRow(tableBody, ticket));
    } catch (error) {
        console.error('Erro ao popular tabela:', error);
    }
}

function searchTickets() {
    const filters = ['ticket', 'status', 'creator', 'startDate', 'endDate'].reduce((acc, fieldId) => {
        acc[fieldId] = document.getElementById(fieldId)?.querySelector('.input-field')?.value || '';
        return acc;
    }, {});

    console.log('Filtros enviados:', filters);
    populateTicketsTable(filters);
}


document.getElementById('search-btn').addEventListener('click', searchTickets);