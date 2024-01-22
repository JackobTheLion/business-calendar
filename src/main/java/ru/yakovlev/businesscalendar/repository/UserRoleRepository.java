package ru.yakovlev.businesscalendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yakovlev.businesscalendar.model.user.UserRole;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findUserRoleByNameIn(List<String> role);

    UserRole findUserRoleByName(String name);
}
