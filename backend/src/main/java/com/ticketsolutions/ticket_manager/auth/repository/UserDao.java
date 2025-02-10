package com.ticketsolutions.ticket_manager.auth.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.ticketsolutions.ticket_manager.auth.domain.User;
import com.ticketsolutions.ticket_manager.core.utils.SQLCreator;
import com.ticketsolutions.ticket_manager.core.utils.SQLFields;

@Repository
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final DataSource dataSource;
	private static final SQLCreator sqlCreator = new SQLCreator("users");

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SuppressWarnings("serial")
    private Connection getConnection() throws DataAccessException {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            logger.error("Erro ao obter conexão com o banco de dados", e);
            throw new DataAccessException("Erro ao obter conexão com o banco de dados", e) {};
        }
    }

    public List<User> findAll() {
        String sql = sqlCreator.findAll();
        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = mapRowToObject(rs);
                users.add(user);
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os usuários", e);
        }

        return users;
    }

    public User findById(Long id) {
        String sql = sqlCreator.findBy("id");
        User user = null;

        try (Connection connection = getConnection(); 
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToObject(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário com ID: " + id, e);
        }

        return user;
    }

    public User findByNameOrEmail(String name, String email) {
        String sql = sqlCreator.findByAny("name", "email");
        User user = null;

        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToObject(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por nome ou email", e);
        }

        return user;
    }

    public User findByName(String name) {
        String sql = sqlCreator.findBy("name");
        User user = null;

        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToObject(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por nome", e);
        }

        return user;
    }

    public User findByEmail(String email) {
        String sql = sqlCreator.findBy("email");
        User user = null;

        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToObject(rs);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário por email", e);
        }

        return user;
    }
    
    public Map<Long, String> getIdToUserNameById(Collection<Long> userIds) {
        Map<Long, String> userIdToUserNameMap = new HashMap<>();
        String placeholders = userIds.stream()
                                     .map(v -> "?")
                                     .collect(Collectors.joining(","));

        String sql = "SELECT id, name FROM users WHERE id IN (" + placeholders + ")";

        try (Connection connection = getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int index = 1;
                for (Long userId : userIds) {
                    stmt.setLong(index++, userId);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Long userId = rs.getLong("id");
                        String userName = rs.getString("name");
                        userIdToUserNameMap.put(userId, userName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários por id", e);
        }

        return userIdToUserNameMap;
    }

    @SuppressWarnings("serial")
    public User save(User user) throws DataAccessException {
        SQLFields sqlFields = null;
        try {
            sqlFields = sqlCreator.prepareFields(user, SQLCreator::convertToSnakeCase);
        } catch (IllegalAccessException e) {
            throw new DataAccessException("Erro ao preparar os campos para o usuário", e) {};
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
                    user.setId(generatedKeys.getLong(1));
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao salvar o usuário", e);
            throw new DataAccessException("Erro ao salvar o usuário no banco de dados", e) {};
        }

        return user;
    }

    @SuppressWarnings("serial")
    public User update(Long id, User userDetails) throws DataAccessException {
        SQLFields sqlFields = null;

        try {
            sqlFields = sqlCreator.prepareFields(userDetails, SQLCreator::convertToSnakeCase);
        } catch (IllegalAccessException e) {
            throw new DataAccessException("Erro ao preparar os campos para o usuário", e) {};
        }

        String sql = sqlCreator.update(sqlFields);

        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < sqlFields.values().size(); i++) {
                stmt.setObject(i + 1, sqlFields.values().get(i));
            }

            stmt.setObject(sqlFields.values().size() + 1, id);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new DataAccessException("Nenhum usuário foi atualizado. Verifique o ID fornecido.") {};
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário com ID: " + id, e);
            throw new DataAccessException("Erro ao atualizar o usuário no banco de dados", e) {};
        }

        return userDetails;
    }

    @SuppressWarnings("serial")
    public void deleteById(Long id) throws DataAccessException {
        String sql = sqlCreator.deleteBy("id");

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Erro ao deletar usuário com ID: " + id, e);
            throw new DataAccessException("Erro ao deletar o usuário no banco de dados", e) {};
        }
    }

    @SuppressWarnings("serial")
	private User mapRowToObject(ResultSet rs) throws DataAccessException {
        try {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            String password = rs.getString("password");
            LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
            String role = rs.getString("role");
            String location = rs.getString("location");

            if (name == null || email == null || birthDate == null || role == null || location == null || password == null) {
                throw new DataAccessException("Required field(s) missing for user") {};
            }

            return new User(id, name, email, password, birthDate, role, location);
        } catch (Exception e) {
            logger.error("Erro ao mapear os dados do usuário", e);
            throw new DataAccessException("Erro ao mapear os dados do usuário", e) {};
        }
    }
}