package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.DatabaseConnection;
import database.DatabaseException;
import enums.BookingStatus;
import model.Booking;

public final class BookingDao {

    private static final BookingDao INSTANCE = new BookingDao();

    private BookingDao() {
    }

    public static BookingDao getInstance() {
        return INSTANCE;
    }

    public long insert(Booking booking) {
        String sql = "INSERT INTO bookings (guest_id, guest_name, room_id, check_in, check_out, status, created_at, "
                + "confirmed_at, cancelled_at, cancelled_by, cancellation_penalty, note) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, booking.getGuestId());
            statement.setString(2, booking.getGuestName());
            statement.setLong(3, booking.getRoomId());
            statement.setString(4, booking.getCheckIn().toString());
            statement.setString(5, booking.getCheckOut().toString());
            statement.setString(6, booking.getStatus().name());
            statement.setString(7, booking.getCreatedAt().toString());
            statement.setString(8, toText(booking.getConfirmedAt()));
            statement.setString(9, toText(booking.getCancelledAt()));
            statement.setString(10, booking.getCancelledBy());
            statement.setString(11, booking.getCancellationPenalty().toPlainString());
            statement.setString(12, booking.getNote());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                booking.setId(id);
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Inserimento prenotazione fallito", e);
        }
    }

    public Optional<Booking> findById(long id) {
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM bookings WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ricerca prenotazione fallita", e);
        }
    }

    public List<Booking> findAll() {
        return query("SELECT * FROM bookings ORDER BY id", null);
    }

    public List<Booking> findByGuestId(long guestId) {
        return query("SELECT * FROM bookings WHERE guest_id = ? ORDER BY id", guestId);
    }

    public void update(Booking booking) {
        String sql = "UPDATE bookings SET check_in = ?, check_out = ?, status = ?, confirmed_at = ?, cancelled_at = ?, "
                + "cancelled_by = ?, cancellation_penalty = ?, note = ? WHERE id = ?";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, booking.getCheckIn().toString());
            statement.setString(2, booking.getCheckOut().toString());
            statement.setString(3, booking.getStatus().name());
            statement.setString(4, toText(booking.getConfirmedAt()));
            statement.setString(5, toText(booking.getCancelledAt()));
            statement.setString(6, booking.getCancelledBy());
            statement.setString(7, booking.getCancellationPenalty().toPlainString());
            statement.setString(8, booking.getNote());
            statement.setLong(9, booking.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Aggiornamento prenotazione fallito", e);
        }
    }

    public boolean delete(long id) {
        try (PreparedStatement statement = connection().prepareStatement("DELETE FROM bookings WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Eliminazione prenotazione fallita", e);
        }
    }

    private List<Booking> query(String sql, Long parameter) {
        List<Booking> bookings = new ArrayList<>();
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            if (parameter != null) {
                statement.setLong(1, parameter);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    bookings.add(map(rs));
                }
            }
            return bookings;
        } catch (SQLException e) {
            throw new DatabaseException("Lettura prenotazioni fallita", e);
        }
    }

    private Booking map(ResultSet rs) throws SQLException {
        Booking booking = new Booking(
                rs.getLong("guest_id"),
                rs.getString("guest_name"),
                rs.getLong("room_id"),
                LocalDate.parse(rs.getString("check_in")),
                LocalDate.parse(rs.getString("check_out")),
                BookingStatus.valueOf(rs.getString("status")),
                LocalDateTime.parse(rs.getString("created_at")),
                rs.getString("note"));
        booking.setId(rs.getLong("id"));
        booking.setConfirmedAt(fromText(rs.getString("confirmed_at")));
        booking.setCancelledAt(fromText(rs.getString("cancelled_at")));
        booking.setCancelledBy(rs.getString("cancelled_by"));
        booking.setCancellationPenalty(new BigDecimal(rs.getString("cancellation_penalty")));
        return booking;
    }

    private static String toText(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    private static LocalDateTime fromText(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }

    private Connection connection() {
        return DatabaseConnection.getInstance().getConnection();
    }
}
