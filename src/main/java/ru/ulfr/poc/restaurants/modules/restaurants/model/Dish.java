package ru.ulfr.poc.restaurants.modules.restaurants.model;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Dish object, contains name, price, date of validity and id of restaurant.
 * <p>
 * Restaurant can be mapped as {@link Restaurant} object, but since we use it for
 * direct data output it will cause complications with wrapping data.
 * <p>
 * In case we want to provide dish by exact request, it would be reasonable to link
 * restaurant as object.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "dishes")
public class Dish implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Name of the dish
     */
    @Column(name = "name")
    private String name;

    /**
     * Price is defined as DECIMAL data type, common practice.
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * In DB this object is defined as DATE, which means that time part is set to 0.
     */
    @Column(name = "valid_on")
    private Date validOn;

    /**
     * Restaurant ID. Could be {@link Restaurant} object if needed
     */
    @Column(name = "restaurant")
    private int restaurantId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getValidOn() {
        return validOn;
    }

    public void setValidOn(Date validOn) {
        this.validOn = validOn;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurant) {
        this.restaurantId = restaurant;
    }
}
