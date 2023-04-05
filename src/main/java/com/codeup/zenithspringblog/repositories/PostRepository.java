package com.codeup.zenithspringblog.repositories;

import com.codeup.zenithspringblog.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Post findByTitle(String post_to_be_deleted);
}
