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
import enums.InvoiceStatus;
import model.Invoice;

public final class InvoiceDao {

    private static final InvoiceDao INSTANCE = new InvoiceDao();

    private InvoiceDao() {
    }

    public static InvoiceDao getInstance() {
        return INSTANCE;
    }

    public long insert(Invoice invoice) {
        String sql = "INSERT INTO invoices (booking_id, guest_id, guest_name, room_number, check_in, check_out, "
                + "issue_date, discount, penalty, total_amount, status, paid_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, invoice.getBookingId());
            statement.setLong(2, invoice.getGuestId());
            statement.setString(3, invoice.getGuestName());
            statement.setString(4, invoice.getRoomNumber());
            statement.setString(5, invoice.getCheckIn().toString());
            statement.setString(6, invoice.getCheckOut().toString());
            statement.setString(7, invoice.getIssueDate().toString());
            statement.setString(8, invoice.getDiscount().toPlainString());
            statement.setString(9, invoice.getPenalty().toPlainString());
            statement.setString(10, invoice.getTotalAmount().toPlainString());
            statement.setString(11, invoice.getStatus().name());
            statement.setString(12, toText(invoice.getPaidAt()));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                invoice.setId(id);
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Inserimento fattura fallito", e);
        }
    }

    public Optional<Invoice> findById(long id) {
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM invoices WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ricerca fattura fallita", e);
        }
    }

    public List<Invoice> findAll() {
        return query("SELECT * FROM invoices ORDER BY id", null);
    }

    public List<Invoice> findByGuestId(long guestId) {
        return query("SELECT * FROM invoices WHERE guest_id = ? ORDER BY id", guestId);
    }

    public void update(Invoice invoice) {
        String sql = "UPDATE invoices SET discount = ?, penalty = ?, total_amount = ?, status = ?, paid_at = ? WHERE id = ?";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, invoice.getDiscount().toPlainString());
            statement.setString(2, invoice.getPenalty().toPlainString());
            statement.setString(3, invoice.getTotalAmount().toPlainString());
            statement.setString(4, invoice.getStatus().name());
            statement.setString(5, toText(invoice.getPaidAt()));
            statement.setLong(6, invoice.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Aggiornamento fattura fallito", e);
        }
    }

    public boolean delete(long id) {
        try (PreparedStatement statement = connection().prepareStatement("DELETE FROM invoices WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Eliminazione fattura fallita", e);
        }
    }

    private List<Invoice> query(String sql, Long parameter) {
        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            if (parameter != null) {
                statement.setLong(1, parameter);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    invoices.add(map(rs));
                }
            }
            return invoices;
        } catch (SQLException e) {
            throw new DatabaseException("Lettura fatture fallita", e);
        }
    }

    private Invoice map(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice(
                rs.getLong("booking_id"),
                rs.getLong("guest_id"),
                rs.getString("guest_name"),
                rs.getString("room_number"),
                LocalDate.parse(rs.getString("check_in")),
                LocalDate.parse(rs.getString("check_out")),
                LocalDateTime.parse(rs.getString("issue_date")),
                new BigDecimal(rs.getString("discount")),
                new BigDecimal(rs.getString("penalty")),
                new BigDecimal(rs.getString("total_amount")),
                InvoiceStatus.valueOf(rs.getString("status")));
        invoice.setId(rs.getLong("id"));
        invoice.setPaidAt(fromText(rs.getString("paid_at")));
        return invoice;
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
