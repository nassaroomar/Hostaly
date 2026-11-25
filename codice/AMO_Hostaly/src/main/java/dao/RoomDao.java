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
import enums.RoomStatus;
import enums.RoomType;
import model.Room;

public final class RoomDao {

    private static final RoomDao INSTANCE = new RoomDao();

    private RoomDao() {
    }

    public static RoomDao getInstance() {
        return INSTANCE;
    }

    public long insert(Room room) {
        String sql = "INSERT INTO rooms (number, floor, type, base_price, max_guests, status, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, room.getNumber());
            statement.setInt(2, room.getFloor());
            statement.setString(3, room.getType().name());
            statement.setString(4, room.getBasePrice().toPlainString());
            statement.setInt(5, room.getMaxGuests());
            statement.setString(6, room.getStatus().name());
            statement.setString(7, room.getNote());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                room.setId(id);
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Inserimento camera fallito", e);
        }
    }

    public Optional<Room> findById(long id) {
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM rooms WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ricerca camera fallita", e);
        }
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM rooms ORDER BY number");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                rooms.add(map(rs));
            }
            return rooms;
        } catch (SQLException e) {
            throw new DatabaseException("Lettura camere fallita", e);
        }
    }

    public void update(Room room) {
        String sql = "UPDATE rooms SET number = ?, floor = ?, type = ?, base_price = ?, max_guests = ?, status = ?, note = ? WHERE id = ?";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, room.getNumber());
            statement.setInt(2, room.getFloor());
            statement.setString(3, room.getType().name());
            statement.setString(4, room.getBasePrice().toPlainString());
            statement.setInt(5, room.getMaxGuests());
            statement.setString(6, room.getStatus().name());
            statement.setString(7, room.getNote());
            statement.setLong(8, room.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Aggiornamento camera fallito", e);
        }
    }

    public boolean delete(long id) {
        try (PreparedStatement statement = connection().prepareStatement("DELETE FROM rooms WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Eliminazione camera fallita", e);
        }
    }

    private Room map(ResultSet rs) throws SQLException {
        Room room = new Room(
                rs.getString("number"),
                rs.getInt("floor"),
                RoomType.valueOf(rs.getString("type")),
                new BigDecimal(rs.getString("base_price")),
                rs.getInt("max_guests"),
                RoomStatus.valueOf(rs.getString("status")),
                rs.getString("note"));
        room.setId(rs.getLong("id"));
        return room;
    }

    private Connection connection() {
        return DatabaseConnection.getInstance().getConnection();
    }
}
