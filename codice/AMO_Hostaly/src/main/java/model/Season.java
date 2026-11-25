package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Season {
    private long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal factor;

    public Season(String name, LocalDate startDate, LocalDate endDate, BigDecimal factor) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.factor = factor;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getFactor() { return factor; }
    public void setFactor(BigDecimal factor) { this.factor = factor; }

    @Override
    public String toString() {
        return name + " (" + startDate + " - " + endDate + ")";
    }
}
