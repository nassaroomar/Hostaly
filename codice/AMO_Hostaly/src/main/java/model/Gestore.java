package model;

import enums.Role;

public class Gestore extends Utente {

    public Gestore(String username, String passwordHash, String fullName, String email) {
        super(username, passwordHash, fullName, email);
    }

    @Override
    public Role getRole() {
        return Role.GESTORE;
    }
}
