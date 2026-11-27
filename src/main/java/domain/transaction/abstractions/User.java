package domain.transaction.abstractions;

import domain.transaction.valueObjects.Cpf;
import domain.transaction.valueObjects.Email;
import domain.transaction.valueObjects.Id;

public class User {
    private Id id;
    private String name;
    private Cpf cpf;
    private Email email;

    public User(String name, String cpf, String email) {
        this.name = name;
        this.cpf = new Cpf(cpf);
        this.email = new Email(email);
    }
}
