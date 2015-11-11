package ru.ulfr.poc.restaurants.modules.restaurants.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by ulfr on 11.11.15.
 */
@Embeddable
public class RestaurantRatingKey implements Serializable {

    @Column(name = "restaurant")
    private int restaurantId;

    @Column(name = "rep_date")
    private Date date;

    public RestaurantRatingKey() {
    }

    public RestaurantRatingKey(int restaurantId, Date date) {
        this.restaurantId = restaurantId;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantRatingKey that = (RestaurantRatingKey) o;

        return restaurantId == that.restaurantId && !(date != null ? !date.equals(that.date) : that.date != null);

    }

    @Override
    public int hashCode() {
        int result = restaurantId;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
