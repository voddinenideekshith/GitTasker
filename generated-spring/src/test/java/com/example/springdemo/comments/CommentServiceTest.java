package com.example.springdemo.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CommentServiceTest {
    private CommentRepository repository;
    private CommentService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(CommentRepository.class);
        service = new CommentService(repository);
    }

    @Test
    void addComment_savesAndReturns() {
        CommentDTO dto = new CommentDTO();
        dto.setTaskId("t1");
        dto.setAuthor("alice");
        dto.setContent("hello");

        Comment saved = new Comment("t1", "alice", "hello");
        saved.setId("c1");
        when(repository.save(any(Comment.class))).thenReturn(saved);

        Comment result = service.addComment(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("c1");
    }

    @Test
    void listByTask_delegatesToRepo() {
        Comment c = new Comment("t1", "bob", "note");
        when(repository.findByTaskId("t1")).thenReturn(List.of(c));

        var list = service.listByTask("t1");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getAuthor()).isEqualTo("bob");
    }
}
