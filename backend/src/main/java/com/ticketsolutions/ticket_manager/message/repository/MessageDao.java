package com.ticketsolutions.ticket_manager.message.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

import com.ticketsolutions.ticket_manager.message.domain.Message;


@Repository
public class MessageDao {

    private final DataSource dataSource;

    public MessageDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Buscar todas as mensagens de um ticket
    public List<Message> findByTicketId(Long ticketId) {
        String sql = "SELECT * FROM messages WHERE ticket_id = ?";
        List<Message> messages = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = mapRowToMessage(rs);
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    // Buscar uma mensagem por ID
    public Optional<Message> findById(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ?";
        Message message = null;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    message = mapRowToMessage(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(message);
    }

    // Salvar uma nova mensagem
    public Message save(Message message) {
        String sql = "INSERT INTO messages (ticket_id, user_id, text, creation_date, creation_time, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, message.getTicketId());
            stmt.setLong(2, message.getUserId());
            stmt.setString(3, message.getText());
            stmt.setDate(4, Date.valueOf(message.getCreationDate()));
            stmt.setTime(5, Time.valueOf(message.getCreationTime()));
            stmt.setString(6, message.getStatus());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return message;
    }

    // Método auxiliar para mapear um ResultSet para Message
    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long ticketId = rs.getLong("ticket_id");
        Long userId = rs.getLong("user_id");
        String text = rs.getString("text");
        Date creationDate = rs.getDate("creation_date");
        Time creationTime = rs.getTime("creation_time");
        String status = rs.getString("status");

        return new Message(id, ticketId, userId, text, creationDate.toLocalDate(), creationTime.toLocalTime(), status);
    }
}