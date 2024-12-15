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
        String sql = "INSERT INTO tickets (title, description, status, user_id, creation_date, update_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE tickets SET title = ?, description = ?, status = ?, user_id = ?, update_date = ? " +
                "WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ticketDetails.getTitle());
            stmt.setString(2, ticketDetails.getDescription());
            stmt.setString(3, ticketDetails.getStatus());
            stmt.setLong(4, ticketDetails.getUserId());
            stmt.setDate(5, Date.valueOf(ticketDetails.getUpdateDate()));
            stmt.setLong(6, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticketDetails;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM tickets WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
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

        if (title == null || description == null || status == null || userId == null || creationDate == null || updateDate == null) {
            throw new SQLException("Required field(s) missing for ticket");
        }

        return new Ticket(id, title, description, status, userId, creationDate, updateDate);
    }
}
