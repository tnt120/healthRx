package com.healthrx.backend.services;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.external.paramters.ParameterRequest;
import com.healthrx.backend.api.internal.Parameter;
import com.healthrx.backend.api.internal.Unit;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.handler.ExceptionResponse;
import com.healthrx.backend.mapper.ParameterMapper;
import com.healthrx.backend.mapper.UserParameterMapper;
import com.healthrx.backend.repository.ParameterLogRepository;
import com.healthrx.backend.repository.ParameterRepository;
import com.healthrx.backend.repository.UnitRepository;
import com.healthrx.backend.repository.UserParameterRepository;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.impl.ParametersServiceImpl;
import com.healthrx.backend.handler.BusinessErrorCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParametersServiceTest {

    @Mock
    private ParameterRepository parameterRepository;

    @Mock
    private UserParameterRepository userParameterRepository;

    @Mock
    private ParameterLogRepository parameterLogRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ParameterMapper parameterMapper;

    @Mock
    private Supplier<User> principalSupplier;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private ParametersServiceImpl parametersService;

    private ParameterRequest parameterRequest;
    private Parameter parameter;
    private Unit unit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        unit = Unit.builder()
                .id("1")
                .name("Kilograms")
                .symbol("kg")
                .build();

        parameterRequest = ParameterRequest.builder()
                .name("Weight")
                .hint("Enter your weight")
                .maxValue(150.0)
                .minValue(30.0)
                .maxStandardValue(120.0)
                .minStandardValue(50.0)
                .unitId("1")
                .build();

        parameter = Parameter.builder()
                .id("param1")
                .name("Weight")
                .hint("Enter your weight")
                .maxValue(150.0)
                .minValue(30.0)
                .maxStandardValue(120.0)
                .minStandardValue(50.0)
                .unit(unit)
                .build();
    }

    @Test
    void shouldAddParameter() {
        when(adminService.checkPermissions()).thenReturn(null);
        when(unitRepository.findById("1")).thenReturn(Optional.of(unit));
        when(parameterRepository.findByName("Weight")).thenReturn(Optional.empty());
        when(parameterRepository.save(any(Parameter.class))).thenReturn(parameter);
        when(parameterMapper.map(any(Parameter.class))).thenReturn(
                ParameterDTO.builder()
                        .id("param1")
                        .name("Weight")
                        .unit("kg")
                        .build()
        );

        ParameterDTO result = parametersService.addParameter(parameterRequest);

        assertNotNull(result);
        assertEquals("param1", result.getId());
        assertEquals("Weight", result.getName());
        assertEquals("kg", result.getUnit());
        verify(parameterRepository, times(1)).save(any(Parameter.class));
    }

    @Test
    void shouldNotAddParameterIfNameExists() {
        when(adminService.checkPermissions()).thenReturn(null);
        when(unitRepository.findById("1")).thenReturn(Optional.of(unit));
        when(parameterRepository.findByName("Weight")).thenReturn(Optional.of(parameter));

        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> parametersService.addParameter(parameterRequest));
        assertEquals(BusinessErrorCodes.PARAMETER_ALREADY_EXISTS.getError().getMessage(), exception.getMessage());
        verify(parameterRepository, never()).save(any(Parameter.class));
    }


    @Test
    void shouldEditParameter() {
        ParameterRequest editRequest = ParameterRequest.builder()
                .name("Updated Weight")
                .hint("Update your weight")
                .build();

        when(adminService.checkPermissions()).thenReturn(null);
        when(parameterRepository.findById("param1")).thenReturn(Optional.of(parameter));
        when(parameterRepository.findByName("Updated Weight")).thenReturn(Optional.empty());
        when(parameterRepository.save(any(Parameter.class))).thenReturn(parameter);
        when(parameterMapper.map(any(Parameter.class))).thenReturn(
                ParameterDTO.builder()
                        .id("param1")
                        .name("Updated Weight")
                        .unit("kg")
                        .build()
        );

        ParameterDTO result = parametersService.editParameter("param1", editRequest);

        assertNotNull(result);
        assertEquals("param1", result.getId());
        assertEquals("Updated Weight", result.getName());
        verify(parameterRepository, times(1)).save(any(Parameter.class));
    }

    @Test
    void shouldDeleteParameter() {
        when(adminService.checkPermissions()).thenReturn(null);
        when(parameterRepository.findById("param1")).thenReturn(Optional.of(parameter));

        Void result = parametersService.deleteParameter("param1");

        assertNull(result);
        verify(parameterLogRepository, times(1)).deleteAllByParameterId("param1");
        verify(userParameterRepository, times(1)).deleteAllByParameterId("param1");
        verify(parameterRepository, times(1)).delete(parameter);
    }

    @Test
    void shouldGetAllUserParameters() {
        User user = User.builder().id("user1").build();
        when(principalSupplier.get()).thenReturn(user);
        when(userParameterRepository.findAllByUserId("user1")).thenReturn(Collections.emptyList());

        List<UserParametersResponse> result = parametersService.getUserParameters();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userParameterRepository, times(1)).findAllByUserId("user1");
    }

    @Test
    void shouldNotDeleteParameterIfNotFound() {
        when(adminService.checkPermissions()).thenReturn(null);
        when(parameterRepository.findById("param1")).thenReturn(Optional.empty());

        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> parametersService.deleteParameter("param1"));
        assertEquals(BusinessErrorCodes.PARAMETER_NOT_FOUND.getError().getMessage(), exception.getMessage());
        verify(parameterRepository, never()).delete(any(Parameter.class));
    }

}
