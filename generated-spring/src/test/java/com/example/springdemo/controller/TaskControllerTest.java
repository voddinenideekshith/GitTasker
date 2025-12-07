package com.example.springdemo.controller;

import com.example.springdemo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskControllerTest {
    @Mock
    private TaskService service;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private TaskController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList() {
        when(service.listAll()).thenReturn(List.of());
        String view = controller.list(null, model);
        assertEquals("tasks/index", view);
        verify(model).addAttribute(eq("tasks"), anyList());
    }

    @Test
    void testCreateForm() {
        String view = controller.createForm(model);
        assertEquals("tasks/form", view);
    }

    @Test
    void testEditFormNotFound() {
        when(service.findById("1")).thenReturn(Optional.empty());
        String view = controller.editForm("1", model, redirectAttributes);
        assertEquals("redirect:/tasks", view);
    }
}
