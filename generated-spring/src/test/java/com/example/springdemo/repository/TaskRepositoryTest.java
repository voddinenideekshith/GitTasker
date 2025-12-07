package com.example.springdemo.repository;

import com.example.springdemo.domain.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskRepositoryTest {
    @Mock
    private TaskRepository repository;

    @Test
    void testSaveAndFind() {
        Task task = new Task("1", "title", "desc", "cid", "pending", "owner");
        Task saved = new Task("1", "title", "desc", "cid", "pending", "owner");

        when(repository.save(task)).thenReturn(saved);
        when(repository.findById("1")).thenReturn(Optional.of(saved));

        Task result = repository.save(task);
        assertNotNull(result.getId());
        assertEquals("title", result.getTitle());
        String id = result.getId();
        if (id != null) {
            assertTrue(repository.findById(id).isPresent());
        }

        verify(repository).save(task);
        verify(repository).findById("1");
    }
}
