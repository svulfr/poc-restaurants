package ru.ulfr.poc.restaurants.modules.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
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
     * Returns current session user based on SecurityContext.
     *
     * @return {@link User} object of current user or null
     */
    protected Account getSessionUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            User user = (User) principal;

            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            Object systemUser = attributes.getAttribute("currentUser", RequestAttributes.SCOPE_SESSION);
            if (systemUser == null || !(systemUser instanceof Account)) {
                systemUser = accountDao.getAccountByLogin(user.getUsername());
                attributes.setAttribute("currentUser", systemUser, RequestAttributes.SCOPE_SESSION);
            }
            return (Account) systemUser;
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
