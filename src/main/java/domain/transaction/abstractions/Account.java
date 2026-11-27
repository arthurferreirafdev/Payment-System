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
    private AccountStatus accountStatus;
    private Money balance;

    public void deposit(Money amount){

        balance.add(amount);
    }
}
