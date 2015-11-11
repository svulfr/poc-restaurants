package ru.ulfr.poc.restaurants.modules.account.dao;

import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.restaurants.modules.account.model.Account;

import java.util.List;

public interface AccountDao {
    Account getAccountByLogin(String login);

    List<Account> list();

    @Transactional
    Account updateAccount(Account account);
}
