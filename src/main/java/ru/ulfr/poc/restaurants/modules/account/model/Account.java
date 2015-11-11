package ru.ulfr.poc.restaurants.modules.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.DigestUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;


/**
 * Represents account.
 */
@Entity
@Table(name = "accounts")
@DynamicUpdate
@SuppressWarnings("unused")
public class Account implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }
}
