package ru.ulfr.poc.restaurants.modules.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.ulfr.poc.restaurants.modules.core.AbstractController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/rest/system")
@SuppressWarnings("unused")
public class SystemREST extends AbstractController {

    @Autowired
    SystemDao systemDao;

    /**
     * Technical method for generating test data
     *
     * @return true
     */
    @RequestMapping(path = "/init", method = RequestMethod.GET)
    public boolean init(HttpServletRequest request) {
        assertPrivileges(request, "ROLE_ADMIN");
        systemDao.rebuildMenu();
        return true;
    }
}
