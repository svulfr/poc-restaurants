package ru.ulfr.poc.restaurants.modules.restaurants.dao;

import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Dish;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Restaurant;

import java.util.List;

/**
 * Interface for restaurants DAO
 */
public interface RestaurantsDao {
    @Transactional
    List<Restaurant> listRestaurantsForToday();

    @Transactional
    void insertDish(Dish dish);

    @Transactional
    boolean vote(int restaurantId, int accountId);

    @Transactional
    void generateTestData();
}
