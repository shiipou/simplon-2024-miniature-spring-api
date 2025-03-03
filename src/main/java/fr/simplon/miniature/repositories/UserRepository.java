package fr.simplon.miniature.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.simplon.miniature.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}
