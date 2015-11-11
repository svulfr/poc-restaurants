package ru.ulfr.poc.restaurants.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.ulfr.poc.restaurants.modules.core.AbstractController;
import ru.ulfr.poc.restaurants.modules.restaurants.dao.RestaurantsDao;

/**
 * Page controller for web pages
 */
@Controller
@RequestMapping(path = "/")
@SuppressWarnings("unused")
public class RootController extends AbstractController {

    @Autowired
    RestaurantsDao restaurantsDao;

    @RequestMapping(path = "/")
    public String index(Model model) {
        model.addAttribute("account", getSessionUser());
        model.addAttribute("menu", restaurantsDao.listRestaurantsForToday());
        return "index";
    }

    @RequestMapping(path = "/login")
    public String login() {
        return "login";
    }
}
