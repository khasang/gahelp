package io.khasang.gahelp.service;

import io.khasang.gahelp.entity.User;

import java.util.List;

public interface UserService {
    /**
     * service for adding new user
     *
     * @param user for adding
     * @return added user
     */
    User add(User user);

    /**
     * service for getting user by id
     *
     * @param id - user's id
     * @return specific user by id
     */
    User getById(long id);

    /**
     * service for getting users by specific name
     *
     * @param name - specific name
     * @return - users with specific name
     */
    List<User> getByName(String name);

    /**
     * service for getting user by login
     *
     * @param login - specific login
     * @return - user with specific login
     */
    User getByLogin(String login);

    /**
     * service for getting blocked user
     * @return list of blocked users
     */
    List<User> getBlocked();

    /**
     * service for getting all users
     *
     * @return all users
     */
    List<User> getAll();

    /**
     * service for deleting user by login
     *
     * @param login - users's login for delete
     * @return deleted user
     */
    User delete(String login);

    /**
     * service for deletting user by id
     * @param id - user's id for delete
     * @return - deleted user
     */
    User deleteById(long id);

    /**
     * ervice for updating user by login
     * @param login - user's login
     * @param user - new user for updating in database
     * @returnc - updating user
     */
    User updateByLogin(String login, User user);

    /**
     * ervice for updating user by id
     * @param id - user's id
     * @param user - new user for updating in database
     * @return - updating user
     */
    User updateById(long id, User user);

}
