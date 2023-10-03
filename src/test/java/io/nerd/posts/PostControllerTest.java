package io.nerd.posts;

import io.nerd.posts.exception.PostNotFoundException;
import io.nerd.posts.post.Post;
import io.nerd.posts.post.PostController;

import io.nerd.posts.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.StringTemplate.STR;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;

    List<Post> posts = new ArrayList<Post>();

    @BeforeEach
    void setUp() {
        posts = List.of(
                new Post(1, 1, "title1", "body1", null),
                new Post(2, 1, "title2", "body2", null)
        );
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id": 1,
                        "userId": 1,
                        "title": "title1",
                        "body": "body1"
                    },
                    {
                        "id": 2,
                        "userId": 1,
                        "title": "title2",
                        "body": "body2"
                    }
                ]
                """;
        when(postRepository.findAll()).thenReturn(posts);
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

    }

    @Test
    void shouldFindPostById() throws Exception {
        String jsonResponse = """
                {
                    "id": 1,
                    "userId": 1,
                    "title": "title1",
                    "body": "body1"
                }
                """;
        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    void shouldNotFindPostById() throws Exception {
        when(postRepository.findById(133)).thenThrow(PostNotFoundException.class);
        mockMvc.perform(get("/api/posts/133"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPostWhenPostIsValid() throws Exception {
        var post = posts.get(0);
        String jsonResponse = """
                {
                    "id": 1,
                    "userId": 1,
                    "title": "title1",
                    "body": "body1"
                }
                """;

        when(postRepository.save(post)).thenReturn(post);
        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(jsonResponse)
                )
                .andExpect(status().isCreated());

    }

    @Test
    void shouldNotCreatePostWhenPostIsInvalid() throws Exception {
        var post = new Post(1, 1, "", "", null);
        String jsonResponse =STR."""
                {
                    "id": \{post.id()},
                    "userId": \{post.userId()},
                    "title": "\{post.title()}",
                    "body": "\{post.body()}",
                    version: null
                }
                """;

        mockMvc.perform(post("/api/posts")
                .contentType("application/json")
                .content(jsonResponse))
                .andExpect(status().isBadRequest());

    }
}



















