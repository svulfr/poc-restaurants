package ru.ulfr.poc.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.ulfr.poc.restaurants.MvcResourcesConfig;
import ru.ulfr.poc.restaurants.PersistenceContext;
import ru.ulfr.poc.restaurants.SecurityConfig;
import ru.ulfr.poc.restaurants.modules.account.dao.AccountDao;
import ru.ulfr.poc.restaurants.modules.account.model.Account;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Dish;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Restaurant;
import ru.ulfr.poc.restaurants.modules.restaurants.model.Vote;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MvcResourcesConfig.class, PersistenceContext.class, SecurityConfig.class})
@WebAppConfiguration
@Rollback(value = false)
public class TestRest {

    /**
     * Spring MVC mock
     */
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    AccountDao accountDao;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @javax.persistence.PersistenceContext
    private EntityManager em;

    private Account randomAccount() {
        return em.createQuery("from Account order by rand()", Account.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    private Account randomAccountNot(Integer... id) {
        return em.createQuery("from Account where id not in (?1) order by rand()", Account.class)
                .setParameter(1, Arrays.asList(id))
                .setMaxResults(1)
                .getSingleResult();
    }

    private Restaurant randomRestaurant() {
        return em.createQuery("from Restaurant order by rand()", Restaurant.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    private Restaurant randomRestaurantNot(Integer... id) {
        return em.createQuery("from Restaurant where id not in (?1) order by rand()", Restaurant.class)
                .setParameter(1, Arrays.asList(id))
                .setMaxResults(1)
                .getSingleResult();
    }

    private MockHttpSession login(Account account) throws Exception {
        String name = account.getLogin();
        String pass = "test";
        MockHttpSession session = (MockHttpSession) mockMvc
                .perform(post("/login")
                        .param("username", name)
                        .param("password", pass))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/"))
                .andReturn()
                .getRequest()
                .getSession();

        Assert.assertNotNull(session);
        return session;
    }

    private MockHttpSession loginAdmin() throws Exception {
        Account admin = new Account();
        admin.setLogin("a@a");
        return login(admin);
    }


    @Test
    public void testListAccounts() throws Exception {
        List<Integer> accountIds = em.createQuery("select id from Account", Integer.class)
                .getResultList();

        MockHttpSession session = loginAdmin();
        mockMvc.perform(get("/rest/account/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(accountIds.size())));
    }

    @Test
    public void testListAccounts403() throws Exception {
        MockHttpSession session = login(randomAccountNot(1));
        mockMvc.perform(get("/rest/account/").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListTodayMenu() throws Exception {
        List<Restaurant> restaurants = em.createQuery("from Restaurant t where (select count(*) from Dish d where d.restaurantId = t.id and d.validOn = current_date) > 0", Restaurant.class)
                .getResultList();

        mockMvc.perform(get("/rest/restaurants/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(restaurants.size())));
    }

    @Test
    public void testAddDish() throws Exception {
        Restaurant r = randomRestaurant();
        long dishesCount = em.createQuery("select count(*) from Dish where restaurantId = ?1 and validOn = current_date ", Long.class)
                .setParameter(1, r.getId())
                .getSingleResult();

        Dish dish = new Dish();
        dish.setName("new-name");
        dish.setPrice(BigDecimal.TEN);

        List<Dish> dishHolder = new ArrayList<>();

        MockHttpSession session = loginAdmin();
        mockMvc.perform(post(String.format("/rest/restaurants/%d/dish", r.getId())).session(session)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.toJSON(dish)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andDo(mvcResult -> dishHolder.add(TestUtil.toBean(Dish.class, mvcResult.getResponse().getContentAsString())));

        // check returned object
        Assert.assertTrue(dishHolder.get(0).getName().equals(dish.getName()));
        Assert.assertTrue(dishHolder.get(0).getPrice().equals(dish.getPrice()));

        // check DB object
        Dish newDish = em.find(Dish.class, dishHolder.get(0).getId());
        Assert.assertNotNull(newDish);
        Assert.assertTrue(newDish.getName().equals(dish.getName()));
        Assert.assertTrue(newDish.getPrice().compareTo(dish.getPrice()) == 0);

        long newDishesCount = em.createQuery("select count(*) from Dish where restaurantId = ?1 and validOn = current_date ", Long.class)
                .setParameter(1, r.getId())
                .getSingleResult();
        Assert.assertTrue(newDishesCount == dishesCount + 1);
    }


    @Test
    public void testAddDish403() throws Exception {
        Dish dish = new Dish();
        dish.setName("new-name");
        dish.setPrice(BigDecimal.TEN);

        mockMvc.perform(post(String.format("/rest/restaurants/%d/dish", randomRestaurant().getId()))
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.toJSON(dish)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void testVoteNoVoted() throws Exception {
        Account account = randomAccountNot(1);
        // wipe votes
        em.createQuery("delete from Vote where accountId = ?1")
                .setParameter(1, account.getId())
                .executeUpdate();
        Restaurant restaurant = randomRestaurant();

        MockHttpSession session = login(account);
        mockMvc.perform(post(String.format("/rest/restaurants/%d/vote", restaurant.getId())).session(session)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Boolean.valueOf(mvcResult.getResponse().getContentAsString()).equals(true));
        em.flush();

        Vote vote = em.find(Vote.class, account.getId());
        Assert.assertNotNull(vote);
        Assert.assertTrue(vote.getRestaurantId() == restaurant.getId());
    }

    @Test
    @Transactional
    public void testVoteVoted() throws Exception {
        Account account = randomAccountNot(1);
        List<Integer> rIds = em.createQuery("select r.id from Restaurant r where r.id in (select v.restaurantId from Vote v where v.accountId = ?1)", Integer.class)
                .setParameter(1, account.getId())
                .getResultList();
        Restaurant restaurantVoted;
        if (rIds.size() == 0) {
            restaurantVoted = randomRestaurant();
            Vote vote = new Vote(account.getId(), restaurantVoted.getId());
            em.persist(vote);
            em.flush();
        } else {
            restaurantVoted = em.find(Restaurant.class, rIds.get(0));
        }

        Restaurant restaurantNotVoted = randomRestaurantNot(restaurantVoted.getId());

        Calendar calendar = Calendar.getInstance();
        boolean allowed = calendar.get(Calendar.HOUR_OF_DAY) < 11;

        MockHttpSession session = login(account);
        mockMvc.perform(post(String.format("/rest/restaurants/%d/vote", restaurantNotVoted.getId())).session(session)
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> Boolean.valueOf(mvcResult.getResponse().getContentAsString()).equals(allowed));
        em.flush();

        Vote vote = em.find(Vote.class, account.getId());
        Assert.assertNotNull(vote);
        if (allowed) {
            Assert.assertTrue(vote.getRestaurantId() == restaurantNotVoted.getId());
        } else {
            Assert.assertTrue(vote.getRestaurantId() == restaurantVoted.getId());
        }
    }

    @Test
    @Transactional
    public void testVote403() throws Exception {
        mockMvc.perform(post(String.format("/rest/restaurants/%d/vote", randomRestaurant().getId()))
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }
}
