package ru.ulfr.poc.restaurants.modules.restaurants.dao;

//import org.hibernate.Filter;
//import org.hibernate.Session;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.restaurants.modules.core.AbstractDao;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Dish;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Restaurant;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Vote;
import ru.ulfr.poc.restaurants.modules.restaurants.model.VoteKey;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Data Access for Restaurants DB. All DB access is localized here, no requests to entity manager/datasource in scope of
 * restaurants is allowed outside this class
 * <p>
 * Class is responsible for data integrity only. All access restrictions are applied in API layer of the system
 */
@Repository
public class RestaurantsDaoImpl extends AbstractDao implements RestaurantsDao {

//    /**
//     * Lists today's menu - list of restaurants having something on menu with dishes
//     * <p>
//     * Hibernate implementation, data fetching and filtering is performed by Hibernate, using filter.
//     * Compact code, however heavy at DB side because of eager fetching of dishes
//     *
//     * @return list of restaurants having dishes on menu today with dished added
//     */
//    @Override
//    @Transactional
//    @SuppressWarnings("unchecked")
//    public List<Restaurant> listRestaurantsForToday() {
//        Session session = em.unwrap(Session.class);
//        Filter filter = session.enableFilter("dishDate");
//        filter.setParameter("validOn", new Date());
//        return session.createQuery("from Restaurant t where (select count(*) from Dish d where d.restaurantId = t.id and d.validOn = current_date) > 0").list();
//    }

    /**
     * Lists today's menu - list of restaurants having something on menu with dishes
     * <p>
     * Another implementation, no Hibernate (just clean JPA) & Java 8, limited to 2 queries to DB
     *
     * @return list of restaurants having dishes on menu today with dished added
     */
    @Override
    @Transactional
    public List<Restaurant> listRestaurantsForToday() {
        // fetch list of restaurants and initialize transient field for
        Map<Integer, Restaurant> restaurantMap = em.createQuery("from Restaurant", Restaurant.class)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(Restaurant::getId, Function.<Restaurant>identity()));
        restaurantMap.values().forEach(r -> r.setDishes(new ArrayList<>()));

        em.createQuery("from Dish where validOn = current_date", Dish.class)
                .getResultList()
                .forEach(d -> restaurantMap.get(d.getRestaurantId()).getDishes().add(d));

        return restaurantMap.values()
                .parallelStream()
                .filter(r -> (r.getDishes().size() > 0))
                .collect(Collectors.toList());
    }

    /**
     * Inserts dish into database
     *
     * @param dish dish to insert
     */
    @Override
    @Transactional
    public void insertDish(Dish dish) {
        em.persist(dish);
    }

    /**
     * Registers user vote
     * <p>
     * implements insert or update operation for vote, including 11:00 restrictions
     *
     * @param restaurantId restaurant ID being voted
     * @param accountId    account which performed vote operation
     * @return true if vote was applied and false otherwise
     */
    @Override
    @Transactional
    public boolean vote(int restaurantId, int accountId) {
        Vote vote = em.find(Vote.class, new VoteKey(restaurantId, accountId));
        if (vote == null) {
            // new vote, persist
            vote = new Vote();
            vote.setVote(new VoteKey(restaurantId, accountId));
            em.persist(vote);
        } else {
            // existing vote, check time and update if needed
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR) >= 11) {
                return false;
            }
            vote.getVote().setRestaurantId(restaurantId);
        }
        return true;
    }

    /**
     * Wipes user votes at 00:00 each day
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupMenu() {
        em.createNativeQuery("TRUNCATE TABLE votes").executeUpdate();
    }


    /**
     * Method is used to create bunch of test dishes for 3 days from today on.
     */
    @Override
    @Transactional
    public void generateTestData() {
        List rawList = em.createNativeQuery("SELECT id FROM restaurants").getResultList();
        int seed = 1000;
        for (int d = 0; d < 3; d++) {
            Date today = new Date(System.currentTimeMillis() + d * 24 * 60 * 60 * 1000);
            for (Object rId : rawList) {
                int restaurantId = (Integer) rId;
                for (int i = 0; i < 2 + Double.valueOf(Math.random() * 2.999).intValue(); i++) {
                    Dish dish = new Dish();
                    dish.setName("DISH SID " + seed++);
                    dish.setPrice(BigDecimal.valueOf(10.0 + Math.random() * 10.0));
                    dish.setRestaurantId(restaurantId);
                    dish.setValidOn(today);
                    em.persist(dish);
                }
            }
        }
    }

}
