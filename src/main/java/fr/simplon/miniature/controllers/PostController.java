package fr.simplon.miniature.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import fr.simplon.miniature.dto.CreateAttachmentDTO;
import fr.simplon.miniature.dto.CreatePostDTO;
import fr.simplon.miniature.models.Group;
import fr.simplon.miniature.models.Post;
import fr.simplon.miniature.repositories.AttachmentRepository;
import fr.simplon.miniature.repositories.GroupRepository;
import fr.simplon.miniature.repositories.PostRepository;
import fr.simplon.miniature.repositories.UserRepository;
import jakarta.security.auth.message.AuthException;
import fr.simplon.miniature.models.Attachment;
import fr.simplon.miniature.models.User;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final AttachmentRepository attachmentRepository;
    
    public PostController(UserRepository userRepository, PostRepository postRepository, GroupRepository groupRepository, AttachmentRepository attachmentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.groupRepository = groupRepository;
        this.attachmentRepository = attachmentRepository;
    }
    
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    @GetMapping("/trending")
    public Page<Post> getTrendingPosts(Pageable pageable) {
        return postRepository.findTrending(pageable);
    }
    
    @GetMapping("/newest")
    public Page<Post> getNewestPosts(Pageable pageable) {
        return postRepository.findNewest(pageable);
    }
    
    @GetMapping("/{id}")
    public Page<Post> getPostById(@PathVariable Long id, Pageable pageable) {
        return postRepository.findCommentsTree(id, pageable);
    }

    @PostMapping
    public Post createPost(
        @RequestBody CreatePostDTO post,
        Authentication authentication
    ) throws AuthException {
        org.springframework.security.core.userdetails.User currentAuthUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User currentUser = userRepository.findByUsername(currentAuthUser.getUsername()).orElseThrow(()->new AuthException("Invalid user"));

        Group group = null;
        if (post.getGroup() != null) {
            group = groupRepository.findById(post.getGroup())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        }
        
        Post newPost = Post.builder()
            .content(post.getContent())
            .owner(currentUser)
            .group(group)
            .build();
            
        Post savedPost = postRepository.saveAndFlush(newPost);
        
        List<CreateAttachmentDTO> attachmentDTOList = post.getAttachments();
        if (attachmentDTOList != null) {
            for (CreateAttachmentDTO attachmentDTO : attachmentDTOList) {
                Attachment attachment = Attachment.builder()
                    .owner(savedPost)
                    .type(attachmentDTO.getType())
                    .link(attachmentDTO.getLink())
                    .image(attachmentDTO.getImage())
                    .video(attachmentDTO.getVideo())
                    .document(attachmentDTO.getDocument())
                    .post(attachmentDTO.getPost() != null ? 
                        postRepository.getReferenceById(attachmentDTO.getPost()) : null)
                    .build();
                    
                Attachment savedAttachment = attachmentRepository.saveAndFlush(attachment);
                savedPost.getAttachments().add(savedAttachment);
            }
        }
        
        return savedPost;
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Post> createComment(
        @PathVariable Long postId,
        @RequestBody CreatePostDTO comment,
        Authentication authentication
    ) throws AuthException {
        Post parentPost = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
            
        org.springframework.security.core.userdetails.User currentAuthUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        User currentUser = userRepository.findByUsername(currentAuthUser.getUsername()).orElseThrow(()->new AuthException("Invalid user"));

        Post newComment = new Post();
        newComment.setContent(comment.getContent());
        newComment.setParent(parentPost);
        newComment.setOwner(currentUser);
        
        Post savedComment = postRepository.saveAndFlush(newComment);
        
        List<CreateAttachmentDTO> attachmentDTOList = comment.getAttachments();
        if (attachmentDTOList != null) {
            for (CreateAttachmentDTO attachmentDTO : attachmentDTOList) {
                Attachment attachment = Attachment.builder()
                    .owner(savedComment)
                    .type(attachmentDTO.getType())
                    .link(attachmentDTO.getLink())
                    .image(attachmentDTO.getImage())
                    .video(attachmentDTO.getVideo())
                    .document(attachmentDTO.getDocument())
                    .post(attachmentDTO.getPost() != null ? 
                        postRepository.getReferenceById(attachmentDTO.getPost()) : null)
                    .build();
                    
                Attachment savedAttachment = attachmentRepository.saveAndFlush(attachment);
                savedComment.getAttachments().add(savedAttachment);
            }
        }

        return ResponseEntity.ok(savedComment);
    }
    
}
