package fr.simplon.miniature.dto;

import lombok.Data;

@Data
public class CreateGroupDTO {
    private String name;
    private boolean isPublic = true;
}
