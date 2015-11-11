package ru.ulfr.poc.restaurants.modules.account.dao;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import ru.ulfr.poc.restaurants.modules.account.model.Account;
import ru.ulfr.poc.restaurants.modules.core.AbstractDao;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Data access for accounts. Only object where access to DB with respect to account is allowed.
 * Maintains account system integrity
 */
public class AccountDaoImpl extends AbstractDao implements AccountDao {

    /**
     * Perform lookup of account by login name.
     *
     * @param login login name
     * @return {@link Account} object if there is matching account, null otherwise
     */
    @Override
    public Account getAccountByLogin(String login) {
        List<Account> accounts = em.createQuery("from Account where login = ?1", Account.class)
                .setParameter(1, login)
                .getResultList();
        return accounts.size() > 0 ? accounts.get(0) : null;
    }

    /**
     * Fetches list of accounts from DB
     *
     * @return list of {@link Account} objects
     */
    @Override
    public List<Account> list() {
        return em.createQuery("from Account", Account.class).getResultList();
    }

    /**
     * Updates fields of account. Non-null field will be applied to the Database
     *
     * @param account account with fields to update set. Null fields will be ignored
     * @return updated object
     */
    @Override
    @Transactional
    public Account updateAccount(Account account) {
        assert account.getId() > 0;
        Account existing = em.find(Account.class, account.getId());
        // enumerate specified fields. May be implemented as customized bean copy procedure
        if (account.getLogin() != null) {
            existing.setLogin(account.getLogin());
        }
        if (account.getPassword() != null) {
            existing.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes(StandardCharsets.UTF_8)));
        }
        // record will be updated when closing transaction.
        return existing;
    }
}

