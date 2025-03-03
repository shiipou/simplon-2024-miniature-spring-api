package fr.simplon.miniature.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.simplon.miniature.models.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * FROM view_post_newest",
    countQuery = "SELECT count(*) FROM view_post_newest",
    nativeQuery = true)
    Page<Post> findNewest(Pageable pageable);

    @Query(value = "SELECT * FROM view_post_trending",
        countQuery = "SELECT count(*) FROM view_post_trending",
        nativeQuery = true)
    Page<Post> findTrending(Pageable pageable);

    @Query(value = "SELECT * FROM GetPostComments(:postId)", 
    countQuery = "SELECT count(*) FROM GetPostComments(:postId)",
    nativeQuery = true)
    Page<Post> findCommentsTree(@Param("postId") Long postId, Pageable pageable);
}
