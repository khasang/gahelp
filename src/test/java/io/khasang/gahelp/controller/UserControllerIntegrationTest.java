package io.khasang.gahelp.controller;

import io.khasang.gahelp.entity.User;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;

public class UserControllerIntegrationTest {
    private static final String ROOT = "http://localhost:8080/users";
    private static final String ADD = "/add";
    private static final String GET_LOGIN = "/get";
    private static final String GET_ID = "/get/id";
    private static final String GET_NAME = "/get/name";
    private static final String GET_BLOCKED = "/get/blocked";
    private static final String ALL = "/all";
    private static final String DELETE_LOGIN = "/delete";
    private static final String DELETE_ID = "/delete/id";
    private static final String UPDATE_LOGIN = "/update";
    private static final String UPDATE_ID = "/update/id";

    private static final String USER_NAME = "New user";

    @Test
    public void checkUpdateLogin() {
        User user = createUser();
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        String oldLogin = user.getLogin();
//        user.setLogin("A" + System.currentTimeMillis());
        user.setName("123");
        user.setRoleId(11);
        user.setIsBlocked(false);

        HttpEntity<User> entity = new HttpEntity<>(user, headers);

        ResponseEntity<User> responseEntityUpdate = template.exchange(
                ROOT + UPDATE_LOGIN + "/{login}",
                HttpMethod.PUT,
                entity,
                User.class,
                oldLogin
        );

        assertEquals(HttpStatus.OK, responseEntityUpdate.getStatusCode());

        ResponseEntity<User> responseEntity = template.exchange(
                ROOT + GET_LOGIN + "/{login}",
                HttpMethod.GET,
                null,
                User.class,
                user.getLogin()
        );

        User updatedUser = responseEntity.getBody();

        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getRoleId(), updatedUser.getRoleId());
        assertEquals(oldLogin, updatedUser.getLogin());
    }

    @Test
    public void checkUpdateId() {
        RestTemplate template = new RestTemplate();
        User user = createUser();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        user.setName("123");
        user.setRoleId(11);
        user.setIsBlocked(false);

        HttpEntity<User> entity = new HttpEntity<>(user, headers);

        ResponseEntity<User> responseEntityUpdate = template.exchange(
                ROOT + UPDATE_ID + "/{id}",
                HttpMethod.PUT,
                entity,
                User.class,
                user.getId()
        );

        assertEquals(HttpStatus.OK, responseEntityUpdate.getStatusCode());

        ResponseEntity<User> responseEntity = template.exchange(
                ROOT + GET_ID + "/{id}",
                HttpMethod.GET,
                null,
                User.class,
                user.getId()
        );

        User updatedUser = responseEntity.getBody();

        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getRoleId(), updatedUser.getRoleId());
    }

    @Test
    public void checkDeleteId() {
        RestTemplate template = new RestTemplate();
        List<User> usersBefore = responseList(template, ROOT + ALL, "");
        User user = createUser();

        ResponseEntity<User> responseEntity = template.exchange(
                ROOT + GET_ID + "/{id}",
                HttpMethod.GET,
                null,
                User.class,
                user.getId()
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        ResponseEntity<User> responseEntityDelete = template.exchange(
                ROOT + DELETE_ID + "/{id}",
                HttpMethod.DELETE,
                null,
                User.class,
                user.getId()
        );
        List<User> usersAfter = responseList(template, ROOT + ALL, "");

        assertEquals(HttpStatus.OK, responseEntityDelete.getStatusCode());
        assertBeforeAfter(usersBefore, usersAfter, 0);
    }

    @Test
    public void checkDeleteLogin() {
        RestTemplate template = new RestTemplate();
        List<User> usersBefore = responseList(template, ROOT + ALL, "");
        User user = createUser();

        ResponseEntity<User> responseEntity = template.exchange(
                ROOT + GET_LOGIN + "/{login}",
                HttpMethod.GET,
                null,
                User.class,
                user.getLogin()
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        ResponseEntity<User> responseEntityDelete = template.exchange(
                ROOT + DELETE_LOGIN + "/{login}",
                HttpMethod.DELETE,
                null,
                User.class,
                user.getLogin()
        );
        List<User> usersAfter = responseList(template, ROOT + ALL, "");

        assertEquals(HttpStatus.OK, responseEntityDelete.getStatusCode());
        assertBeforeAfter(usersBefore, usersAfter, 0);
    }

    @Test
    public void checkGetBlocked() {
        RestTemplate template = new RestTemplate();

        List<User> usersBefore = responseList(template, ROOT + GET_BLOCKED, "");

        createUser();
        createUser();

        List<User> usersAfter = responseList(template, ROOT + GET_BLOCKED, "");

        assertBeforeAfter(usersBefore, usersAfter, 2);
    }

    @Test
    public void checkGetName() {
        RestTemplate template = new RestTemplate();

        List<User> usersBefore = responseList(template, ROOT + GET_NAME, USER_NAME);

        createUser();
        createUser();

        List<User> usersAfter = responseList(template, ROOT + GET_NAME, USER_NAME);

        assertBeforeAfter(usersBefore, usersAfter, 2);
    }

    @Test
    public void checkUserAddGetLoginGetId() {
        User user = createUser();

        RestTemplate template = new RestTemplate();

        ResponseEntity<User> responseEntityId = template.exchange(
                ROOT + GET_ID + "/{id}",
                HttpMethod.GET,
                null,
                User.class,
                user.getId()
        );

        ResponseEntity<User> responseEntityLogin = template.exchange(
                ROOT + GET_LOGIN + "/{login}",
                HttpMethod.GET,
                null,
                User.class,
                user.getLogin()
        );

        assertEquals(HttpStatus.OK, responseEntityId.getStatusCode());
        assertEquals(HttpStatus.OK, responseEntityLogin.getStatusCode());
        assertNotNull(responseEntityId.getBody());
        assertNotNull(responseEntityLogin.getBody());
    }

    @Test
    public void checkAllUsers() {
        RestTemplate template = new RestTemplate();

        List<User> usersBefore = responseList(template, ROOT + ALL, "");

        createUser();
        createUser();

        List<User> usersAfter = responseList(template, ROOT + ALL, "");

        assertBeforeAfter(usersBefore, usersAfter, 2);
    }

    private void assertBeforeAfter(List<User> before, List<User> after, int diffSize) {
        assertNotNull(before);
        assertNotNull(after);
        assertEquals(before.size() + diffSize, after.size());
    }

    private List<User> responseList(RestTemplate template, String url, String value) {
        if (!value.isEmpty())
            value = "/" + value;

        ResponseEntity<List<User>> responseEntityAfter = template.exchange(
                url + value,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {
                },
                value
        );
        return responseEntityAfter.getBody();
    }

    private User createUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String login = "login" + System.currentTimeMillis();

        User user = prefillUser(login);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        RestTemplate restTemplate = new RestTemplate();
        User createdUser = restTemplate.exchange(
                ROOT + ADD,
                HttpMethod.POST,
                entity,
                User.class
        ).getBody();

        assertNotNull(createdUser);
        assertEquals(login, createdUser.getLogin());
        return createdUser;
    }

    private User prefillUser(String login) {
        User user = new User();
        user.setLogin(login);
        user.setName(USER_NAME);
        user.setIsBlocked(true);
        user.setRoleId(3);
        user.setPassword("pass");
        return user;
    }
}
