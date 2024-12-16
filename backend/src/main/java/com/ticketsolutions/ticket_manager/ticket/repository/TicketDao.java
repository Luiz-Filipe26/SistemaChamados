package com.ticketsolutions.ticket_manager.ticket.repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.ticketsolutions.ticket_manager.ticket.domain.Ticket;

@Repository
public class TicketDao {

	private final DataSource dataSource;

	public TicketDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public List<Ticket> findAll() {
		String sql = "SELECT * FROM tickets";
		List<Ticket> tickets = new ArrayList<>();

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				Ticket ticket = mapRowToTicket(rs);
				tickets.add(ticket);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return tickets;
	}

	public Optional<Ticket> findById(Long id) {
		String sql = "SELECT * FROM tickets WHERE id = ?";
		Ticket ticket = null;

		try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ticket = mapRowToTicket(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.ofNullable(ticket);
	}

	public Ticket save(Ticket ticket) {
		String sql = "INSERT INTO tickets (title, description, status, user_id, creation_date, update_date) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, ticket.getTitle());
			stmt.setString(2, ticket.getDescription());
			stmt.setString(3, ticket.getStatus());
			stmt.setLong(4, ticket.getUserId());
			stmt.setDate(5, Date.valueOf(ticket.getCreationDate()));
			stmt.setDate(6, Date.valueOf(ticket.getUpdateDate()));
			stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					ticket.setId(generatedKeys.getLong(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ticket;
	}

	public Ticket update(Long id, Ticket ticketDetails) {
	    StringBuilder sql = new StringBuilder("UPDATE tickets SET ");
	    
	    List<Object> params = new ArrayList<>();
	    
	    if (ticketDetails.getTitle() != null) {
	        sql.append("title = ?, ");
	        params.add(ticketDetails.getTitle());
	    }
	    if (ticketDetails.getDescription() != null) {
	        sql.append("description = ?, ");
	        params.add(ticketDetails.getDescription());
	    }
	    if (ticketDetails.getStatus() != null) {
	        sql.append("status = ?, ");
	        params.add(ticketDetails.getStatus());
	    }
	    if (ticketDetails.getUserId() != null) {
	        sql.append("user_id = ?, ");
	        params.add(ticketDetails.getUserId());
	    }
	    if (ticketDetails.getCreationDate() != null) {
	        sql.append("creation_date = ?, ");
	        params.add(Date.valueOf(ticketDetails.getCreationDate()));
	    }
	    if (ticketDetails.getUpdateDate() != null) {
	        sql.append("update_date = ?, ");
	        params.add(Date.valueOf(ticketDetails.getUpdateDate()));
	    }

	    // Remover a última vírgula e espaço
	    sql.setLength(sql.length() - 2);
	    
	    sql.append(" WHERE id = ?");
	    params.add(id); // Adiciona o ID no final da lista de parâmetros
	    
	    try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
	        for (int i = 0; i < params.size(); i++) {
	            stmt.setObject(i + 1, params.get(i));
	        }
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return ticketDetails;
	}

	public void deleteById(Long id) {
		String sql = "DELETE FROM tickets WHERE id = ?";
		try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Ticket mapRowToTicket(ResultSet rs) throws SQLException {
		Long id = rs.getLong("id");
		String title = rs.getString("title");
		String description = rs.getString("description");
		String status = rs.getString("status");
		Long userId = rs.getLong("user_id");
		LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
		LocalDate updateDate = rs.getDate("update_date").toLocalDate();

		if (title == null || description == null || status == null || userId == null || creationDate == null
				|| updateDate == null) {
			throw new SQLException("Required field(s) missing for ticket");
		}

		return new Ticket(id, title, description, status, userId, creationDate, updateDate);
	}
}
