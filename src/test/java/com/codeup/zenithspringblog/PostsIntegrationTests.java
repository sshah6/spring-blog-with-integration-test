package com.codeup.zenithspringblog;

import com.codeup.zenithspringblog.models.Post;
import com.codeup.zenithspringblog.models.User;
import com.codeup.zenithspringblog.repositories.PostRepository;
import com.codeup.zenithspringblog.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZenithSpringBlogApplication.class)
@AutoConfigureMockMvc
public class PostsIntegrationTests {

    @Autowired
    private MockMvc mvc;

    private HttpSession httpSession;

    @Autowired
    UserRepository userDao;

    @Autowired
    PostRepository postDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() throws Exception {

        // create test user if none exists in the database
        User testUser = userDao.findByUsername("testUser");
        if (testUser == null) {
            testUser = new User("testUser", "testuser@email.com", passwordEncoder.encode("getmein"));
            userDao.save(testUser);
        }

        // Throws a Post request to /login and expect a redirection to the Ads index page after being logged in
        httpSession = this.mvc.perform(post("/login").with(csrf())
                        .param("username", "testUser")
                        .param("password", "getmein"))
                .andExpect(status().is(HttpStatus.FOUND.value()))
                .andExpect(redirectedUrl("/posts"))
                .andReturn()
                .getRequest()
                .getSession();
    }


    // sanity test
    @Test
    public void testIfContextLoads() {
        assertNotNull(mvc);
    }


    // sanity test
    @Test
    public void testIfSessionCreated() {
        assertNotNull(httpSession);
    }


    @Test
    public void testCreatePost() throws Exception {
        // Makes a Post request to /posts/create and expect a redirection to the Post
        this.mvc.perform(
                        post("/posts").with(csrf())
                                .session((MockHttpSession) httpSession)
                                // Add all the required parameters to your request like this
                                .param("title", "My Integration Test Post")
                                .param("body", "This is the body of the integration test post"))
                .andExpect(status().is3xxRedirection()); // expect on successful post creation, app will redirect user to new page
    }

    @Test
    public void testShowPost() throws Exception {

        Post existingPost = postDao.findAll().get(0);

        // Makes a Get request to /posts/{id} and expect a redirection to the Post show page
        this.mvc.perform(get("/posts/" + existingPost.getId()))
                .andExpect(status().isOk())
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingPost.getBody())));
    }

    @Test
    public void testPostsIndex() throws Exception {
        Post existingPost = postDao.findAll().get(0);

        // Makes a Get request to /posts and verifies that we get some of the static text of the posts/index.html template and at least the title from the first Post is present in the template.
        this.mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                // Test the static content of the page
                .andExpect(content().string(containsString("Posts")))
                // Test the dynamic content of the page
                .andExpect(content().string(containsString(existingPost.getTitle())));
    }

    @Test
    public void testDeletePost() throws Exception {
        // Creates a test Post to be deleted
        this.mvc.perform(
                        post("/posts").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("title", "Post to be deleted")
                                .param("body", "Won't last long!"))
                .andExpect(status().is3xxRedirection());

        // Get the recent Post that matches the title
        Post existingPost = postDao.findByTitle("Post to be deleted");

        // Makes a Post request to /posts/{id}/delete and expect a redirection to the Posts index
        this.mvc.perform(
                        post("/posts/" + existingPost.getId() + "/delete").with(csrf())
                                .session((MockHttpSession) httpSession)
                                .param("id", String.valueOf(existingPost.getId())))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testEditPost() throws Exception {

        // Create post to edit
        this.mvc.perform(
            post("/posts").with(csrf())
                .session((MockHttpSession) httpSession)
                .param("title", "Post to edit")
                .param("body", "This is a post that will be edited"))
                .andExpect(status().is3xxRedirection());

        // Get the recent Post that matches the title
        Post existingPost = postDao.findByTitle("Post to edit");

        this.mvc.perform(
            post("/posts/" + existingPost.getId() + "/edit").with(csrf())
                .session((MockHttpSession) httpSession)
                .param("title", "Post to edit")
                .param("body", "THIS IS THE EDITED BODY"))
            .andExpect(status().is3xxRedirection());

        // Makes a Post request to /posts/{id}/delete and expect a redirection to the Posts index
        this.mvc.perform(
                post("/posts/" + existingPost.getId() + "/delete").with(csrf())
                    .session((MockHttpSession) httpSession)
                    .param("id", String.valueOf(existingPost.getId())))
            .andExpect(status().is3xxRedirection());

    }

    @After
    public void tearDown() {
        Post createdPost = postDao.findByTitle("My Integration Test Post");
        if (createdPost != null) {
            postDao.deleteById(createdPost.getId());
        }
    }

}
