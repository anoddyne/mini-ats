package ru.practice.mini_ats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
