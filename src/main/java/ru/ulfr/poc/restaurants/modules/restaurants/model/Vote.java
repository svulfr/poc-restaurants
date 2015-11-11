package ru.ulfr.poc.restaurants.modules.restaurants.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents vote.
 * <p>
 * {@link VoteKey} object is used as key for the object, combining account to restaurant mapping.
 * <p>
 * Can have vote time (if needed).
 */
@Entity
@Table(name = "votes")
public class Vote {
    @Id
    private VoteKey vote;

    public VoteKey getVote() {
        return vote;
    }

    public void setVote(VoteKey vote) {
        this.vote = vote;
    }
}
