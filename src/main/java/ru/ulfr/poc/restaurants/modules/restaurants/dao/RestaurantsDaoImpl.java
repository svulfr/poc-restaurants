package ru.ulfr.poc.restaurants.modules.restaurants.dao;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.restaurants.modules.core.AbstractDao;
import ru.ulfr.poc.restaurants.modules.core.HTTP422Exception;
import ru.ulfr.poc.restaurants.modules.core.HTTP500Exception;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Dish;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Restaurant;
import ru.ulfr.poc.restaurants.modules.restaurants.model.RestaurantRating;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Vote;

import javax.persistence.PersistenceException;
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
        try {
            em.persist(dish);
        } catch (Throwable x) {
            throw translateException(x, "unable to add dish:");
        }
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
        try {
            // find votes by user. Use query as votes table contains max 1 vote.
            List<Vote> votes = em.createQuery("from Vote v where v.accountId = ?1", Vote.class)
                    .setParameter(1, accountId)
                    .getResultList();
            if (votes.size() == 0) {
                // use didn't vote so far
                Vote vote = new Vote(accountId, restaurantId);
                em.persist(vote);
            } else {
                // user already voted, check time and update if needed
                Calendar calendar = Calendar.getInstance();
                if (calendar.get(Calendar.HOUR_OF_DAY) >= 11) {
                    return false;
                }
                votes.get(0).setRestaurantId(restaurantId);
            }
            return true;
        } catch (Throwable x) {
            throw translateException(x, "unable to vote for restaurant:");
        }
    }

    /**
     * Wipes user votes at 00:00 each day
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupMenu() {
        // fetch votes registered during last day
        List<Vote> votes = em.createQuery("from Vote ", Vote.class).getResultList();

        // arrange votes, map restaurant to number of votes, when they are
        Map<Integer, Integer> restaurantVotes = new HashMap<>();
        votes.forEach(v -> restaurantVotes.compute(v.getRestaurantId(),
                (r, c) -> c == null ? 1 : c + 1));

        // it is assumed that we run next day, so
        Calendar yesterday = GregorianCalendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        // insert results into rating table
        restaurantVotes.forEach((r, c) -> em.persist(new RestaurantRating(r, yesterday.getTime(), ((double) c) / votes.size())));

        // truncate votes table, as we don't keep votes
        em.createNativeQuery("TRUNCATE TABLE votes").executeUpdate();
    }

    private RuntimeException translateException(Throwable x, String message) {
        if (x instanceof PersistenceException && x.getCause() instanceof ConstraintViolationException) {
            logger.warn(message + x.getCause().toString());
            return new HTTP422Exception(x.getCause().toString());
        } else {
            logger.warn(message + x.toString());
            return new HTTP500Exception();
        }

    }
}
