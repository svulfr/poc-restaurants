package ru.ulfr.poc.restaurants.modules.system;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ulfr on 24.11.15.
 */
public interface SystemDao {
    @Transactional
    @Scheduled(cron = "1 0 0 * * ?")
    void rebuildMenu();
}
