package dao;

import java.math.BigDecimal;
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
import enums.ServiceCategory;
import model.Service;

public final class ServiceDao {

    private static final ServiceDao INSTANCE = new ServiceDao();

    private ServiceDao() {
    }

    public static ServiceDao getInstance() {
        return INSTANCE;
    }

    public long insert(Service service) {
        String sql = "INSERT INTO extra_services (name, category, price, description, active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, service.getName());
            statement.setString(2, service.getCategory().name());
            statement.setString(3, service.getPrice().toPlainString());
            statement.setString(4, service.getDescription());
            statement.setInt(5, service.isActive() ? 1 : 0);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                service.setId(id);
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Inserimento servizio fallito", e);
        }
    }

    public Optional<Service> findById(long id) {
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM extra_services WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ricerca servizio fallita", e);
        }
    }

    public List<Service> findAll() {
        List<Service> services = new ArrayList<>();
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM extra_services ORDER BY id");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                services.add(map(rs));
            }
            return services;
        } catch (SQLException e) {
            throw new DatabaseException("Lettura servizi fallita", e);
        }
    }

    public void update(Service service) {
        String sql = "UPDATE extra_services SET name = ?, category = ?, price = ?, description = ?, active = ? WHERE id = ?";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, service.getName());
            statement.setString(2, service.getCategory().name());
            statement.setString(3, service.getPrice().toPlainString());
            statement.setString(4, service.getDescription());
            statement.setInt(5, service.isActive() ? 1 : 0);
            statement.setLong(6, service.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Aggiornamento servizio fallito", e);
        }
    }

    public boolean delete(long id) {
        try (PreparedStatement statement = connection().prepareStatement("DELETE FROM extra_services WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Eliminazione servizio fallita", e);
        }
    }

    private Service map(ResultSet rs) throws SQLException {
        Service service = new Service(
                rs.getString("name"),
                ServiceCategory.valueOf(rs.getString("category")),
                new BigDecimal(rs.getString("price")),
                rs.getString("description"));
        service.setId(rs.getLong("id"));
        service.setActive(rs.getInt("active") == 1);
        return service;
    }

    private Connection connection() {
        return DatabaseConnection.getInstance().getConnection();
    }
}
