package ru.ulfr.poc.restaurants.modules.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ru.ulfr.poc.restaurants.modules.account.dao.AccountDao;
import ru.ulfr.poc.restaurants.modules.account.model.Account;

import javax.servlet.http.HttpServletRequest;

/**
 * Root controller providing basic methods, potentially needed by any controller in the application
 */
public class AbstractController {

    @Autowired
    AccountDao accountDao;

    /**
     * Retuns current session user based on SecurityContext.
     *
     * @return {@link Account} object of current user or null
     */
    protected Account getSessionUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            return accountDao.getAccountByLogin(user.getUsername());
        } else {
            return null;
        }
    }

    /**
     * Performs check that user has role specified. Technically needed to reduce code size in controllers
     *
     * @param request calling request
     * @param role    required role
     */
    protected void assertPrivileges(HttpServletRequest request, String role) {
        if (!request.isUserInRole(role)) {
            throw new HTTP403Exception();
        }
    }
}
