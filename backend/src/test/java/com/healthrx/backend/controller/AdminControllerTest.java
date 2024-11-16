package com.healthrx.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthrx.backend.api.external.admin.DashboardDataResponse;
import com.healthrx.backend.service.ActivityService;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.ParametersService;
import com.healthrx.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @Mock
    private UserService userService;

    @Mock
    private ParametersService parametersService;

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private AdminController adminController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnDashboardData() throws Exception {
        DashboardDataResponse response = DashboardDataResponse.builder()
                .users(100)
                .doctors(50)
                .activities(50)
                .drugs(20000)
                .parameters(25)
                .build();

        when(adminService.getDashboardData()).thenReturn(response);

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users").value(100))
                .andExpect(jsonPath("$.doctors").value(50))
                .andExpect(jsonPath("$.activities").value(50))
                .andExpect(jsonPath("$.drugs").value(20000))
                .andExpect(jsonPath("$.parameters").value(25))
                .andReturn();
    }
}
