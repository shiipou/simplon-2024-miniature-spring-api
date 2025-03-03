package fr.simplon.miniature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.simplon.miniature.models.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
