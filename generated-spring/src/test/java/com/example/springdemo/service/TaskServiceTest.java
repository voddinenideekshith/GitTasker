package com.example.springdemo.service;

import com.example.springdemo.domain.Task;
import com.example.springdemo.dto.TaskDto;
import com.example.springdemo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    @Mock
    private TaskRepository repository;
    @Mock
    private IpfsService ipfsService;
    @InjectMocks
    private TaskService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @SuppressWarnings("null")
    @Test
    void testCreateTask() throws Exception {
        TaskDto dto = new TaskDto();
        dto.setTitle("Test");
        dto.setDescription("Desc");
        String owner = "user";
        String cid = "cid123";
        when(ipfsService.add(anyString())).thenReturn(cid);
        Task saved = new Task("1", dto.getTitle(), dto.getDescription(), cid, "pending", owner);
        Task savedTask = repository.save(saved);
        when(repository.save(any(Task.class))).thenReturn(savedTask);
        Task result = service.create(dto, owner);
        assertEquals(cid, result.getCid());
        assertEquals("Test", result.getTitle());
    }

    @Test
    void testFindById() {
        Task task = new Task("1", "title", "desc", "cid123", "pending", "owner");
        when(repository.findById("1")).thenReturn(Optional.of(task));
        Optional<Task> result = service.findById("1");
        assertTrue(result.isPresent());
        assertEquals("title", result.get().getTitle());
    }

    @Test
    void testListAll() {
        Task t1 = new Task("1", "t1", "d1", "cid1", "pending", "owner");
        Task t2 = new Task("2", "t2", "d2", "cid2", "pending", "owner");
        when(repository.findAll()).thenReturn(List.of(t1, t2));
        List<Task> result = service.listAll();
        assertEquals(2, result.size());
    }
}
