package ru.ulfr.poc.restaurants.modules.restaurants;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ulfr.poc.restaurants.modules.account.model.Account;
import ru.ulfr.poc.restaurants.modules.core.AbstractController;
import ru.ulfr.poc.restaurants.modules.core.HTTP500Exception;
import ru.ulfr.poc.restaurants.modules.restaurants.dao.RestaurantsDao;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Dish;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Restaurant;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/rest/restaurants")
@SuppressWarnings("unused")
public class RestaurantsREST extends AbstractController {

    @Autowired
    RestaurantsDao restaurantsDao;

    /**
     * Returns menu for today
     * <p>
     * Access: public
     *
     * @return list of restaurants having dishes on menu today
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public List<Restaurant> listTodayMenu() {
        return restaurantsDao.listRestaurantsForToday();
    }

    /**
     * add dish for specific restaurant. Dish is accepted as JSON in request body
     * <p>
     * Access: admin
     *
     * @param restaurantId restaurant to add dish for
     * @param dish         dish object (restaurantId is overridden by REST path property
     * @return added Dish object
     */
    @RequestMapping(path = "/{restaurantId}/dish", method = RequestMethod.POST)
    public Dish addDish(@PathVariable int restaurantId,
                        @RequestBody Dish dish,
                        HttpServletRequest request) {
        // assert privilege level
        assertPrivileges(request, "ROLE_ADMIN");

        // adjust object and pass to DAO
        dish.setRestaurantId(restaurantId);
        dish.setValidOn(new Date());
        restaurantsDao.insertDish(dish);
        return dish;
    }

    /**
     * Vote for restaurant
     * <p>
     * Access: user
     *
     * @param restaurantId restaurant ID to vote for
     * @return <code>true</code> if vote is succeeded, <code>false</code> otherwise
     */
    @RequestMapping(path = "/{restaurantId}/vote", method = RequestMethod.POST)
    public boolean vote(@PathVariable int restaurantId,
                        HttpServletRequest request) {
        // assert privilege level
        assertPrivileges(request, "ROLE_USER");

        // pass request to DAO
        Account account = getSessionUser();
        if (account == null) {
            // since we have method secured, account will never be null.
            // still throw exception because it means server code inconsistency
            throw new HTTP500Exception();
        }

        return restaurantsDao.vote(restaurantId, account.getId());
    }
}
