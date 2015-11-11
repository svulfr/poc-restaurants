package ru.ulfr.poc.restaurants.modules.restaurants.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents restaurant rating
 */
@Entity
@Table(name = "restaurants_rating")
public class RestaurantRating implements Serializable {
    @Id
    private RestaurantRatingKey key;

    @Column(name = "rating")
    private double rating;

    public RestaurantRating() {
    }

    public RestaurantRating(int restaurantId, Date date, double rating) {
        this.key = new RestaurantRatingKey(restaurantId, date);
        this.rating = rating;
    }

    public RestaurantRatingKey getKey() {
        return key;
    }

    public void setKey(RestaurantRatingKey key) {
        this.key = key;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
