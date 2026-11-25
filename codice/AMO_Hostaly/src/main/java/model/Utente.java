package model;

import enums.Role;

public abstract class Utente {
    private long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private boolean active;

    public Utente(String username, String passwordHash, String fullName, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.active = true;
    }

    public abstract Role getRole();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return fullName + " (" + getClass().getSimpleName() + ")";
    }
}
