package model;

import java.math.BigDecimal;

import enums.ServiceCategory;

public class Service {
    private long id;
    private String name;
    private ServiceCategory category;
    private BigDecimal price;
    private String description;
    private boolean active;

    public Service(String name, ServiceCategory category, BigDecimal price, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.active = true;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ServiceCategory getCategory() { return category; }
    public void setCategory(ServiceCategory category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}
