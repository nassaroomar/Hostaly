package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.DatabaseConnection;
import database.DatabaseException;
import model.Season;

public final class SeasonDao {

    private static final SeasonDao INSTANCE = new SeasonDao();

    private SeasonDao() {
    }

    public static SeasonDao getInstance() {
        return INSTANCE;
    }

    public long insert(Season season) {
        String sql = "INSERT INTO seasons (name, start_date, end_date, factor) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, season.getName());
            statement.setString(2, season.getStartDate().toString());
            statement.setString(3, season.getEndDate().toString());
            statement.setString(4, season.getFactor().toPlainString());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                long id = keys.getLong(1);
                season.setId(id);
                return id;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Inserimento stagione fallito", e);
        }
    }

    public Optional<Season> findById(long id) {
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM seasons WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ricerca stagione fallita", e);
        }
    }

    public List<Season> findAll() {
        List<Season> seasons = new ArrayList<>();
        try (PreparedStatement statement = connection().prepareStatement("SELECT * FROM seasons ORDER BY start_date");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                seasons.add(map(rs));
            }
            return seasons;
        } catch (SQLException e) {
            throw new DatabaseException("Lettura stagioni fallita", e);
        }
    }

    public void update(Season season) {
        String sql = "UPDATE seasons SET name = ?, start_date = ?, end_date = ?, factor = ? WHERE id = ?";
        try (PreparedStatement statement = connection().prepareStatement(sql)) {
            statement.setString(1, season.getName());
            statement.setString(2, season.getStartDate().toString());
            statement.setString(3, season.getEndDate().toString());
            statement.setString(4, season.getFactor().toPlainString());
            statement.setLong(5, season.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Aggiornamento stagione fallito", e);
        }
    }

    public boolean delete(long id) {
        try (PreparedStatement statement = connection().prepareStatement("DELETE FROM seasons WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Eliminazione stagione fallita", e);
        }
    }

    private Season map(ResultSet rs) throws SQLException {
        Season season = new Season(
                rs.getString("name"),
                LocalDate.parse(rs.getString("start_date")),
                LocalDate.parse(rs.getString("end_date")),
                new BigDecimal(rs.getString("factor")));
        season.setId(rs.getLong("id"));
        return season;
    }

    private Connection connection() {
        return DatabaseConnection.getInstance().getConnection();
    }
}
