package fr.simplon.miniature.dto;

import fr.simplon.miniature.models.AttachmentType;
import lombok.Data;

@Data
public class CreateAttachmentDTO {
    private AttachmentType type;
    private String link;
    private String image;
    private String video;
    private String document;
    private Long post;
}
