package model;

import enums.Role;

public class Admin extends Utente {

    public Admin(String username, String passwordHash, String fullName, String email) {
        super(username, passwordHash, fullName, email);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
