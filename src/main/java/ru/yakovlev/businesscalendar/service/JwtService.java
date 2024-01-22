package ru.yakovlev.businesscalendar.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.yakovlev.businesscalendar.model.user.User;

/**
 * JWT service is responsible for work with JWT tokens, including generation, checking validity and extracting user
 * name from token.
 */
public interface JwtService {

    /**
     * Extracting username from token.
     *
     * @param token
     * @return extracted username
     */
    String extractUserName(String token);

    /**
     * Token generation based on user credentials.
     *
     * @param userDetails {@link User}
     * @return generated token
     */
    String generateToken(UserDetails userDetails);

    /**
     * Checks validity of provided token
     *
     * @param token       token
     * @param userDetails {@link User}
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
