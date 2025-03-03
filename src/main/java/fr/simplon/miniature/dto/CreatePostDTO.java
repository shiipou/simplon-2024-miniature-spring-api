package fr.simplon.miniature.dto;

import java.util.List;

import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
public class CreatePostDTO {
    private String content;
    private Long group;
    @OneToMany
    private List<CreateAttachmentDTO> attachments;
}
