package model;

import enums.Role;

public class Cliente extends Utente {

    public Cliente(String username, String passwordHash, String fullName, String email) {
        super(username, passwordHash, fullName, email);
    }

    @Override
    public Role getRole() {
        return Role.CLIENT;
    }
}
