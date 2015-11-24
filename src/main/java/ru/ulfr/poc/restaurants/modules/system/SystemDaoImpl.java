package ru.ulfr.poc.restaurants.modules.system;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ulfr.poc.restaurants.modules.core.AbstractDao;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Dish;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Repository for scheduled imitation of admin actions
 */
@Repository
public class SystemDaoImpl extends AbstractDao implements SystemDao {

    /**
     * Imitates admin actions on building menus for next day
     */
    @Override
    @Transactional
    @Scheduled(cron = "1 0 0 * * ?")
    public void rebuildMenu() {
        List<Integer> ids = em.createQuery("select id from Restaurant", Integer.class)
                .getResultList();

        // cleanup, then generate dishes
        em.createNativeQuery("TRUNCATE TABLE dishes")
                .executeUpdate();
        int seed = 1000;
        for (int d = 0; d < 3; d++) {
            Date today = new Date(System.currentTimeMillis() + d * 24 * 60 * 60 * 1000);
            for (Integer restaurantId : ids) {
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
