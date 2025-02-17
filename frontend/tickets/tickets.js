import RequestConfig from '/assets/scripts/RequestConfig.js';
import FetchInterceptor from '/assets/scripts/FetchInterceptor.js';

FetchInterceptor.create(FetchInterceptor.handleUnauthorized);
let ticketsData = []; // Armazena os tickets recuperados globalmente

document.getElementById('search-btn').onclick = searchTickets;

function searchTickets() {
    const filters = ['ticket', 'status', 'creator', 'startDate', 'endDate']
        .map(fieldId => {
            let value = document.getElementById(fieldId)?.querySelector('.input-field')?.value || '';

            if (!isNaN(value) && value.trim() !== '') value = Number(value);
            if (fieldId === 'startDate' || fieldId === 'endDate') value = value ? new Date(value) : null;

            return { filterName: fieldId, value };
        })
        .filter(filter => filter.value);

    filters.forEach(filter => {
        if (filter.filterName == 'ticket') filter.filterName = 'id';
        if (filter.filterName == 'creator') filter.filterName = 'userName';
    });

    console.log('Filtros enviados:', filters);
    populateTicketsTable(filters);
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

        const tickets = await response.json();
        return tickets.filter(ticket =>
            filters.every(({ filterName, value }) => {
                if (filterName === 'startDate' || filterName === 'endDate') {
                    const ticketDate = new Date(ticket.creationDate);
                    return filterName === 'startDate' ? ticketDate >= value : ticketDate <= value;
                }

                if (!ticket[filterName]) return true;  // Ignora se o valor não existe

                if (typeof ticket[filterName] === 'number') return ticket[filterName] === value;

                if (typeof ticket[filterName] === 'string') {
                    return ticket[filterName].toLowerCase().includes(value.toLowerCase());
                }

                return true;
            })
        );

    } catch (error) {
        console.error(error);
        return [];
    }
}

function createRow(tableBody, ticket) {
    const row = document.createElement('tr');

    row.innerHTML = `
        <td>${String(ticket.id).padStart(6, '0')}</td>
        <td>${ticket.description}</td>
        <td>${ticket.creationDate}</td>
        <td>${ticket.updateDate}</td>
        <td>${ticket.userName}</td>
        <td>${ticket.status}</td>
    `;

    row.addEventListener('click', () => {
        const targetUrl = `/ticket/?ticketId=${ticket.id}`;
        window.open(targetUrl, '_blank');
    });

    tableBody.appendChild(row);
}

