package ru.ulfr.poc.restaurants.modules.restaurants.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Custom combined key for vote.
 * <p>
 * Includes mapping of account-restaurant pair.
 * <p>
 * Since this object is used for comparisons, hashCode and equals methods are overridden.
 */
@Embeddable
public class VoteKey implements Serializable {

    @Column(name = "restaurant")
    private int restaurantId;

    @Column(name = "account")
    private int accountId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteKey voteKey = (VoteKey) o;

        return restaurantId == voteKey.restaurantId && accountId == voteKey.accountId;
    }

    @Override
    public int hashCode() {
        int result = restaurantId;
        result = 31 * result + accountId;
        return result;
    }

    /**
     * Default constructor. Needed to allow instantiation in presence of custom constructor
     */
    public VoteKey() {
    }

    /**
     * Constructor allowing to specify fields.
     *
     * @param restaurantId restaurant ID
     * @param accountId    account ID
     */
    public VoteKey(int restaurantId, int accountId) {
        this.restaurantId = restaurantId;
        this.accountId = accountId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
