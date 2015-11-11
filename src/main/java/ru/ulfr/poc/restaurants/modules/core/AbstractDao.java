package ru.ulfr.poc.restaurants.modules.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * Root implementation for DAO. Provides DataSource and EntityManager
 */
@SuppressWarnings("unused")
public class AbstractDao {
    /**
     * logger
     */
    protected Logger logger = LogManager.getLogger(getClass());

    /**
     * DataSource reference in case we want to use plain JDBC
     */
    @Autowired
    protected DataSource dataSource;

    /**
     * JPA Entity Manager for general use
     */
    @PersistenceContext
    protected EntityManager em;
}
