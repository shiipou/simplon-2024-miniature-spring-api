package fr.simplon.miniature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.simplon.miniature.models.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
