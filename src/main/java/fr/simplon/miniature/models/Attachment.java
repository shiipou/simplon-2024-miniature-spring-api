package fr.simplon.miniature.models;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"attachment\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner")
    @JsonIdentityReference(alwaysAsId = true)
    private Post owner;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "attachment_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AttachmentType type;

    private String link;
    private String image;
    private String video;
    private String document;

    @ManyToOne
    @JoinColumn(name = "post")
    private Post post;
}
