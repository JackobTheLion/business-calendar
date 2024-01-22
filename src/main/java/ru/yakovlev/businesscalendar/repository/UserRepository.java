package ru.yakovlev.businesscalendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yakovlev.businesscalendar.model.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String name);

}
