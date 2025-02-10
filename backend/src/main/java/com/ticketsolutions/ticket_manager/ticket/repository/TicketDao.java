package com.ticketsolutions.ticket_manager.ticket.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.ticketsolutions.ticket_manager.core.utils.SQLCreator;
import com.ticketsolutions.ticket_manager.core.utils.SQLFields;
import com.ticketsolutions.ticket_manager.ticket.domain.Ticket;

@Repository
public class TicketDao {
	
	private static final Logger logger = LoggerFactory.getLogger(TicketDao.class);
	private final DataSource dataSource;
	private static final SQLCreator sqlCreator = new SQLCreator("tickets");

	public TicketDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@SuppressWarnings("serial")
	private Connection getConnection() throws DataAccessException {
		try {
			return dataSource.getConnection();
		} catch (Exception e) {
			throw new DataAccessException("Erro ao obter conexão com o banco de dados", e) {};
		}
	}

	public List<Ticket> findAll() {
		String sql = sqlCreator.findAll();
		List<Ticket> tickets = new ArrayList<>();

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Ticket ticket = mapRowToObject(rs);
				tickets.add(ticket);
			}
		} catch (Exception e) {
			logger.error("Erro ao buscar todos os tickets", e);
		}

		return tickets;
	}

	public Ticket findById(Long id) {
	    String sql = sqlCreator.findBy("id");
	    Ticket ticket = null;

	    try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setLong(1, id);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                ticket = mapRowToObject(rs);
	            }
	        }
	    } catch (Exception e) {
	    	logger.error("Erro ao buscar ticket com ID: " + id, e);
	    }

	    return ticket;
	}

	@SuppressWarnings("serial")
	public Ticket save(Ticket ticket) throws DataAccessException {

	    SQLFields sqlFields = null;
	    try {
	        sqlFields = sqlCreator.prepareFields(ticket, SQLCreator::convertToSnakeCase);
	    } catch (IllegalAccessException e) {
	        throw new DataAccessException("Erro ao preparar os campos para o ticket", e) {};
	    }

	    String sql = sqlCreator.insert(sqlFields);
	    
	    
	    try (Connection connection = getConnection();
	         PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

	        // Preenche os valores no PreparedStatement
	        for (int i = 0; i < sqlFields.values().size(); i++) {
	            stmt.setObject(i + 1, sqlFields.values().get(i));
	        }

	        stmt.executeUpdate();

	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                ticket.setId(generatedKeys.getLong(1));
	            }
	        }
	    } catch (Exception e) {
	    	logger.error("Erro ao salvar o ticket", e);
	        throw new DataAccessException("Erro ao salvar o ticket no banco de dados", e) {};
	    }

	    return ticket;
	}

	@SuppressWarnings("serial")
	public Ticket update(Long id, Ticket ticketDetails) throws DataAccessException {
		SQLFields sqlFields = null;
		
		try {
			sqlFields = sqlCreator.prepareFields(ticketDetails, SQLCreator::convertToSnakeCase);
		} catch (IllegalAccessException e) {
			throw new DataAccessException("Erro ao preparar os campos para o ticket", e) {};
		}
		
	    String sql = sqlCreator.update(sqlFields);

		try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			for (int i = 0; i < sqlFields.values().size(); i++) {
				stmt.setObject(i + 1, sqlFields.values().get(i));
			}

			stmt.setObject(sqlFields.values().size() + 1, id);

			int rowsUpdated = stmt.executeUpdate();
			
			if (rowsUpdated == 0) {
				throw new DataAccessException("Nenhum ticket foi atualizado. Verifique o ID fornecido.") {};
			}
		} catch (Exception e) {
			logger.error("Erro ao atualizar ticket com ID: " + id, e);
			throw new DataAccessException("Erro ao atualizar o ticket no banco de dados", e) {};
		}
		
		return ticketDetails;
	}
	
	@SuppressWarnings("serial")
	public void deleteById(Long id) throws DataAccessException {
	    String sql = sqlCreator.deleteBy("id");
	    try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setLong(1, id);
	        int rowsDeleted = stmt.executeUpdate();

	        if (rowsDeleted == 0) {
	            throw new DataAccessException("Nenhum ticket foi deletado. Verifique o ID fornecido.") {};
	        }
	    } catch (Exception e) {
	    	logger.error("Erro ao deletar ticket com ID: " + id, e);
	        throw new DataAccessException("Erro ao deletar o ticket no banco de dados", e) {};
	    }
	}

	@SuppressWarnings("serial")
	private Ticket mapRowToObject(ResultSet rs) throws DataAccessException {
		try {
			Long id = rs.getLong("id");
			String title = rs.getString("title");
			String description = rs.getString("description");
			String status = rs.getString("status");
			Long userId = rs.getLong("user_id");
			LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
			LocalDate updateDate = rs.getDate("update_date").toLocalDate();

			if (title == null || description == null || status == null || userId == null || creationDate == null
					|| updateDate == null) {
				throw new DataAccessException("Required field(s) missing for ticket") {};
			}

			return new Ticket(id, title, description, status, userId, creationDate, updateDate);
		} catch (Exception e) {
            logger.error("Erro ao mapear os dados do ticket", e);
			throw new DataAccessException("Erro ao mapear os dados do ticket", e) {};
		}
	}
}