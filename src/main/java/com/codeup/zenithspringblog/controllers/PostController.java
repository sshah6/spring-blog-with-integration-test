package com.codeup.zenithspringblog.controllers;

import com.codeup.zenithspringblog.models.Post;
import com.codeup.zenithspringblog.models.User;
import com.codeup.zenithspringblog.repositories.PostRepository;
import com.codeup.zenithspringblog.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PostController {
    private PostRepository postsDao;
    private UserRepository userDao;

    public PostController(PostRepository postsDao, UserRepository userDao) {
        this.postsDao = postsDao;
        this.userDao = userDao;
    }
//A getrequest that takes ue to index file
    @GetMapping("/posts")
    public String returnPosts(Model model) {
        model.addAttribute("posts", postsDao.findAll());
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String returnPost(@PathVariable Long id, Model model) {
        Optional<Post> optionalPost = postsDao.findById(id);
        if (optionalPost.isPresent()) {
            Post post = postsDao.findById(id).get();
            model.addAttribute("post", post);
            return "posts/show";
        } else {
            return "redirect:/posts?error=notfound";
        }
    }

    @GetMapping("/posts/create")
    public String returnPostCreateForm(Model model) {
        model.addAttribute("newPost", new Post());
        return "posts/create";
    }


    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user.getId());
        // create post in the database...
        post.setUser(userDao.findById(user.getId()).get());
        postsDao.save(post);

        // redirect the user to the view displaying all the posts
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditForm(@PathVariable long id, Model model) {
        Post postToEdit = postsDao.findById(id).get();
        model.addAttribute("postToEdit", postToEdit);
        return "posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String submitEditForm(@ModelAttribute Post postUpdates) {
        // pulling out the existing record from the db and setting the fields based on the form post object to avoid losing any existing data/relationships in database
        Post postToUpdate = postsDao.findById(postUpdates.getId()).get();
        postToUpdate.setTitle(postUpdates.getTitle());
        postToUpdate.setBody(postUpdates.getBody());
        postsDao.save(postToUpdate);
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable long id) {
        postsDao.deleteById(id);
        return "redirect:/posts";
    }

}









