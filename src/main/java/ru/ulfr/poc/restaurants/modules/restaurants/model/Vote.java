package ru.ulfr.poc.restaurants.modules.restaurants.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents vote.
 * Can have vote time (if needed).
 */
@Entity
@Table(name = "votes")
@DynamicUpdate
public class Vote {
    @Id
    @Column(name = "account")
    private int accountId;

    @Column(name = "restaurant")
    private int restaurantId;

    public Vote() {
    }

    public Vote(int accountId, int restaurantId) {
        this.accountId = accountId;
        this.restaurantId = restaurantId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }
}
