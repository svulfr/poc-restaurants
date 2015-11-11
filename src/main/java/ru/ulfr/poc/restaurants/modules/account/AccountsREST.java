package ru.ulfr.poc.restaurants.modules.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.ulfr.poc.restaurants.modules.account.dao.AccountDao;
import ru.ulfr.poc.restaurants.modules.account.model.Account;
import ru.ulfr.poc.restaurants.modules.core.AbstractController;

import java.util.List;

/**
 * REST interface for account management
 */
@RestController
@RequestMapping(path = "/rest/account")
@SuppressWarnings("unused")
public class AccountsREST extends AbstractController {

    /**
     * Autowired reference to account singleton bean
     */
    @Autowired
    AccountDao accountDao;


    /**
     * Returns list of accounts
     * <p>
     * Access: admin
     *
     * @return list of {@link Account} objects
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Account> listAccounts() {
        return accountDao.list();
    }

    /**
     * Updates caller's password. Password is passed as part of incomplete account object.
     *
     * @param account account object with password specified
     * @return true
     */
    @RequestMapping(path = "/password", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public Account updatePassword(@RequestBody Account account) {
        Account caller = getSessionUser();
        caller.setPassword(account.getPassword());
        return accountDao.updateAccount(caller);
    }

}
