package domain.transaction.abstractions;

import domain.transaction.enums.AccountStatus;
import domain.transaction.valueObjects.AccountNumber;
import domain.transaction.valueObjects.BankCode;
import domain.transaction.valueObjects.Id;
import domain.transaction.valueObjects.Money;

public class Account {
    private Id id;
    private User user;
    private AccountNumber accountNumber;
    private BankCode bankCode;
    private AccountStatus status;
    private Money balance;

    public void deposit(Money amount){
        ValidateAccountStatus();
        balance.add(amount);
    }

    public void withdraw(Money amount){
        ValidateAccountStatus();

        if (balance.isLessThan(amount)) {
            throw new IllegalStateException(
                    "Saldo insuficiente"
            );
        }

        balance.subtract(amount);
    }

    private void ValidateAccountStatus() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "A Conta não está ativa"
            );
        }
    }
}
