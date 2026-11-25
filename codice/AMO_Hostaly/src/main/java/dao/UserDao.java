package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.DatabaseConnection;
import database.DatabaseException;
import enums.Role;
import model.Admin;
import model.Cliente;
import model.Gestore;
import model.Utente;

public final class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    private UserDao() {
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }

    public long insert(Utente user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getRole().name());
            statement.setInt(6, user.isActive() ? 1 : 0);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                user.setId(id);
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Inserimento utente fallito", e);
        }
    }

    public Optional<Utente> findById(long id) {
        return findOne("SELECT * FROM users WHERE id = ?", id);
    }

    public Optional<Utente> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ricerca utente fallita", e);
        }
    }

    public List<Utente> findAll() {
        List<Utente> users = new ArrayList<>();
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM users ORDER BY id");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                users.add(map(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Lettura utenti fallita", e);
        }
    }

    public void update(Utente user) {
        String sql = "UPDATE users SET password_hash = ?, full_name = ?, email = ?, active = ? WHERE id = ?";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, user.getPasswordHash());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getEmail());
            statement.setInt(4, user.isActive() ? 1 : 0);
            statement.setLong(5, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Aggiornamento utente fallito", e);
        }
    }

    public boolean delete(long id) {
        try (PreparedStatement statement = connection().prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Eliminazione utente fallita", e);
        }
    }

    public long count() {
        try (PreparedStatement statement = connection().prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet rs = statement.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Conteggio utenti fallito", e);
        }
    }

    private Optional<Utente> findOne(String sql, long id) {
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ricerca utente fallita", e);
        }
    }

    private Utente map(ResultSet rs) throws SQLException {
        Role role = Role.valueOf(rs.getString("role"));
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        Utente user = switch (role) {
            case CLIENT -> new Cliente(username, passwordHash, fullName, email);
            case GESTORE -> new Gestore(username, passwordHash, fullName, email);
            case ADMIN -> new Admin(username, passwordHash, fullName, email);
        };
        user.setId(rs.getLong("id"));
        user.setActive(rs.getInt("active") == 1);
        return user;
    }

    private Connection connection() {
        return DatabaseConnection.getInstance().getConnection();
    }
}
