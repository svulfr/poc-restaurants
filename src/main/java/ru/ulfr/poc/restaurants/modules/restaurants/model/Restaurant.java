package ru.ulfr.poc.restaurants.modules.restaurants.model;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Represents Restaurant.
 * <p>
 * Two implementations are made, one uses filters of Hibernate (commented out) for fetching dishes (exact
 * meaning - either full dishes list, or filtered list for today). Another transient field, set by DAO object.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "restaurants")
@DynamicUpdate
// Hibernate implementation would use this filter to automatically fetch disheson specified day
//
//@FilterDef(name = "dishDate", parameters = {
//        @ParamDef(name = "validOn", type = "date")
//})
public class Restaurant implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Restaurant name
     */
    @Column(name = "name")
    private String name;


//    Hibernate implementation would use eager fetching of dishes based on filter. Positive is simplicity of code.
//    Negative is that eager fetching of OneToMany collections turns into sequence of SELECT queries (one query for
//    list of restaurants and one query for each restaurant
//
//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name = "restaurant")
//    @Filter(name = "dishDate", condition = ":validOn = valid_on")
//    private List<Dish> dishes;

    /**
     * List of dishes, non-DB, added by DAO.
     */
//    Java/JPA implementation will fill dishes with single query
    @Transient
    List<Dish> dishes;

    /**
     * Uses Hibernate annotation for custom column query. Alternative (denormalize) would be to set votes as regular column
     * and maintain its value in DAO or with trigger & stored procedure inside DB (depending on policies)
     */
    // language=MySQL
    @Formula("(SELECT COUNT(*) FROM votes v WHERE v.restaurant = id)")
    private int votes;

    /**
     * Rating field, collects sum or average ratings of the restaurant for last week.
     * Will be set to null when no positive ratings are registered for the restaurant
     */
    // language=MySQL
    @Formula("(SELECT SUM(r.rating) FROM restaurants_rating r WHERE r.restaurant = id AND DATEDIFF(CURRENT_DATE, r.rep_date) < 7)")
    private Double rating;

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

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public int getVotes() {
        return votes;
    }

    public Double getRating() {
        return rating;
    }
}
