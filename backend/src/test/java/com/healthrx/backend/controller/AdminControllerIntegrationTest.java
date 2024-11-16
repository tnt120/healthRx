package com.healthrx.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnDashboardDataWhenAuthorized() throws Exception {
        setAuthenticatedUser("test@test.com", Role.ADMIN);

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users").exists())
                .andExpect(jsonPath("$.doctors").exists())
                .andExpect(jsonPath("$.activities").exists())
                .andExpect(jsonPath("$.drugs").exists())
                .andExpect(jsonPath("$.parameters").exists());
    }

    @Test
    void shouldReturnForbiddenWhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotAdmin() throws Exception {
        setAuthenticatedUser("", Role.USER);
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    private void setAuthenticatedUser(String email, Role role) {
        User adminUser = User.builder()
                .email(email)
                .password("password")
                .role(role)
                .build();

        when(userRepository.findUserByEmail(email))
                .thenReturn(Optional.of(adminUser));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                adminUser, null, adminUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
