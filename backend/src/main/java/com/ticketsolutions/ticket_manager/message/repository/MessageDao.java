package com.ticketsolutions.ticket_manager.message.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.ticketsolutions.ticket_manager.core.utils.SQLCreator;
import com.ticketsolutions.ticket_manager.core.utils.SQLFields;
import com.ticketsolutions.ticket_manager.message.domain.Message;

@Repository
public class MessageDao {

    private static final Logger logger = LoggerFactory.getLogger(MessageDao.class);
    private final DataSource dataSource;
    private static final SQLCreator sqlCreator = new SQLCreator("messages");

    public MessageDao(DataSource dataSource) {
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

    public List<Message> findAll() {
        String sql = sqlCreator.findAll();
        List<Message> messages = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Message message = mapRowToObject(rs);
                messages.add(message);
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as mensagens", e);
        }

        return messages;
    }

    public Message findById(Long id) {
        String sql = sqlCreator.findBy("id");
        Message message = null;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    message = mapRowToObject(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar mensagem com ID: " + id, e);
        }

        return message;
    }
    
    @SuppressWarnings("serial")
    public List<Message> findByTicketId(Long ticketId) {
        String sql = sqlCreator.findBy("ticket_id");
        List<Message> messages = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message message = mapRowToObject(rs);
                    messages.add(message);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar mensagens para o ticket com ID: " + ticketId, e);
            throw new DataAccessException("Erro ao buscar mensagens para o ticket com ID: " + ticketId, e) {};
        }

        return messages;
    }

    @SuppressWarnings("serial")
    public Message save(Message message) throws DataAccessException {
        SQLFields sqlFields;

        try {
            sqlFields = sqlCreator.prepareFields(message, SQLCreator::convertToSnakeCase);
        } catch (IllegalAccessException e) {
            throw new DataAccessException("Erro ao preparar os campos para a mensagem", e) {};
        }

        String sql = sqlCreator.insert(sqlFields);

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < sqlFields.values().size(); i++) {
                stmt.setObject(i + 1, sqlFields.values().get(i));
            }

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    message.setId(generatedKeys.getLong(1));
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao salvar a mensagem", e);
            throw new DataAccessException("Erro ao salvar a mensagem no banco de dados", e) {};
        }

        return message;
    }

    @SuppressWarnings("serial")
    public Message update(Long id, Message messageDetails) throws DataAccessException {
        SQLFields sqlFields;

        try {
            sqlFields = sqlCreator.prepareFields(messageDetails, SQLCreator::convertToSnakeCase);
        } catch (IllegalAccessException e) {
            throw new DataAccessException("Erro ao preparar os campos para a mensagem", e) {};
        }

        String sql = sqlCreator.update(sqlFields);

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < sqlFields.values().size(); i++) {
                stmt.setObject(i + 1, sqlFields.values().get(i));
            }

            stmt.setObject(sqlFields.values().size() + 1, id);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DataAccessException("Nenhuma mensagem foi atualizada. Verifique o ID fornecido.") {};
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar mensagem com ID: " + id, e);
            throw new DataAccessException("Erro ao atualizar a mensagem no banco de dados", e) {};
        }

        return messageDetails;
    }

    @SuppressWarnings("serial")
    public void deleteById(Long id) throws DataAccessException {
        String sql = sqlCreator.deleteBy("id");
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted == 0) {
                throw new DataAccessException("Nenhuma mensagem foi deletada. Verifique o ID fornecido.") {};
            }
        } catch (Exception e) {
            logger.error("Erro ao deletar mensagem com ID: " + id, e);
            throw new DataAccessException("Erro ao deletar a mensagem no banco de dados", e) {};
        }
    }

    @SuppressWarnings("serial")
    private Message mapRowToObject(ResultSet rs) throws DataAccessException {
        try {
            Long id = rs.getLong("id");
            Long ticketId = rs.getLong("ticket_id");
            Long userId = rs.getLong("user_id");
            String text = rs.getString("text");
            LocalDate creationDate = rs.getDate("creation_date").toLocalDate();
            LocalTime creationTime = rs.getTime("creation_time").toLocalTime();
            String status = rs.getString("status");

            if (text == null || status == null || creationDate == null || creationTime == null) {
                throw new DataAccessException("Required field(s) missing for message") {};
            }

            return new Message(id, ticketId, userId, text, creationDate, creationTime, status);
        } catch (Exception e) {
            logger.error("Erro ao mapear os dados da mensagem", e);
            throw new DataAccessException("Erro ao mapear os dados da mensagem", e) {};
        }
    }

}
