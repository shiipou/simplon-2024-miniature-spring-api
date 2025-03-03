package fr.simplon.miniature.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Builder.Default;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "\"post\"")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Reference to the owner (user)
    @ManyToOne
    @NonNull
    @JoinColumn(name = "owner")
    private User owner;
    
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "\"group\"")
    @JsonIdentityReference(alwaysAsId = true)
    private Group group;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "parent")
    @JsonIdentityReference(alwaysAsId = true)
    private Post parent;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Nullable
    @Default
    private List<Attachment> attachments = new ArrayList<>();

    @Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Default
    @Column(name = "is_draft")
    private Boolean isDraft = false;

    @Default
    @ManyToMany
    @JoinTable(name = "\"like\"",
        joinColumns = @JoinColumn(name = "post", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "\"user\"", referencedColumnName = "id"))
    private List<User> likes = new ArrayList<>();
}
