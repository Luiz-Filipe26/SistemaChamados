package com.ticketsolutions.ticket_manager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import com.ticketsolutions.ticket_manager.model.User;

@Repository
public class UserDao {

	private final DataSource dataSource;

	public UserDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public List<User> getAllUsers() {
		String sql = "SELECT * FROM users";
		List<User> users = new ArrayList<>();

		try (Connection connection = getConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				User user = mapRowToUser(rs);
				users.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return users;
	}

	public Optional<User> getUserById(Long id) {
		String sql = "SELECT * FROM users WHERE id = ?";
		User user = null;

		try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					user = mapRowToUser(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return Optional.ofNullable(user);
	}

	public Optional<User> getUserByNameOrEmail(String name, String email) {
		String sql = "SELECT * FROM users WHERE name = ? OR email = ?";
		try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, email);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					User user = mapRowToUser(rs);
					return Optional.of(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public User createUser(User user) {
		String sql = "INSERT INTO users (name, email, birth_date, role, location) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getEmail());
			stmt.setDate(3, java.sql.Date.valueOf(user.getBirthDate()));
			stmt.setString(4, user.getRole());
			stmt.setString(5, user.getLocation());
			stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					user.setId(generatedKeys.getLong(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User updateUser(Long id, User userDetails) {
		String sql = "UPDATE users SET birth_date = ?, role = ?, location = ? WHERE id = ?";
		try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setDate(1, java.sql.Date.valueOf(userDetails.getBirthDate()));
			stmt.setString(2, userDetails.getRole());
			stmt.setString(3, userDetails.getLocation());
			stmt.setLong(4, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userDetails;
	}

    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	private User mapRowToUser(ResultSet rs) throws SQLException {
		Long id = rs.getLong("id");
		String name = rs.getString("name");
		String email = rs.getString("email");
		java.sql.Date birthDate = rs.getDate("birth_date");
		String role = rs.getString("role");
		String location = rs.getString("location");

		if (name == null || email == null || birthDate == null || role == null || location == null) {
			throw new SQLException("Required field(s) missing for user");
		}

		return new User(id, name, email, birthDate.toLocalDate(), role, location);
	}
}