package com.healthrx.backend.services;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.mapper.DrugMapper;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.service.impl.DrugsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrugServiceTest {

    @Mock
    private DrugRepository drugRepository;
    @Mock
    private DrugPackRepository drugPackRepository;
    @Mock
    private UserDrugRepository userDrugRepository;
    @Mock
    private DrugLogRepository drugLogRepository;
    @Mock
    private DrugMapper drugMapper;
    @Mock
    private Supplier<User> principalSupplier;

    @InjectMocks
    private DrugsServiceImpl drugsService;

    @Test
    void shouldGetAllDrugsWhenEmpty() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Drug> emptyDrugPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(drugRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyDrugPage); // Fix Specification

        PageResponse<DrugResponse> result = drugsService.getAllDrugs(0, 10, "name", "asc", null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
    }


    @Test
    void shouldGetDrugsFromUser() {
        User user = User.builder().id("1").build();
        UserDrug userDrug = UserDrug.builder().drug(Drug.builder().id(1).name("Paracetamol").build()).build();
        DrugResponse drugResponse = DrugResponse.builder().id(1).name("Paracetamol").build();

        when(principalSupplier.get()).thenReturn(user);
        when(userDrugRepository.findAllByUserId("1")).thenReturn(List.of(userDrug));
        when(drugMapper.simpleMap(userDrug.getDrug())).thenReturn(drugResponse);

        List<DrugResponse> result = drugsService.getDrugsFromUser();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(drugResponse, result.get(0));
    }

    @Test
    void shouldReturnEmptyDrugsForUserWhenNoneExist() {
        User user = User.builder().id("1").build();

        when(principalSupplier.get()).thenReturn(user);
        when(userDrugRepository.findAllByUserId("1")).thenReturn(Collections.emptyList());

        List<DrugResponse> result = drugsService.getDrugsFromUser();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyUserDrugsWhenNoneExist() {
        User user = User.builder().id("1").build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<UserDrug> emptyUserDrugPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(principalSupplier.get()).thenReturn(user);
        when(userDrugRepository.findByUser(eq(user), eq(pageable))).thenReturn(emptyUserDrugPage);

        PageResponse<UserDrugsResponse> result = drugsService.getUserDrugs(0, 10, "id", "asc");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalElements());
    }

    @Test
    void shouldGetUserDrugMonitor() {
        User user = User.builder().id("1").build();
        Days today = Days.from(LocalDate.now().getDayOfWeek());
        Drug drug = Drug.builder().id(1).name("Paracetamol").build();
        List<DrugDoseDay> doseDays = List.of(DrugDoseDay.builder().day(today).build());
        List<DrugDoseTime> doseTimes = List.of(DrugDoseTime.builder().doseTime(LocalTime.of(10, 0)).build());
        UserDrug userDrug = UserDrug.builder()
                .id("1")
                .doseSize(2)
                .drug(drug)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .drugDoseDays(doseDays)
                .drugDoseTimes(doseTimes)
                .build();
        DrugLog drugLog = DrugLog.builder().takenTime(LocalTime.of(10, 0)).build();

        when(principalSupplier.get()).thenReturn(user);
        when(userDrugRepository.findAllByUserId("1")).thenReturn(List.of(userDrug));
        when(drugLogRepository.findDrugLogByDrugIdAndUserIdAndTimeToday(1, "1", LocalTime.of(10, 0)))
                .thenReturn(Optional.of(drugLog));
        when(drugPackRepository.findPackUnitByDrugId(1)).thenReturn(List.of("mg"));
        when(drugMapper.map(eq(userDrug), eq("mg"), eq(LocalTime.of(10, 0)), eq(drugLog.getTakenTime())))
                .thenReturn(UserDrugMonitorResponse.builder().id("1").build());

        List<UserDrugMonitorResponse> result = drugsService.getUserDrugMonitor();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("1", result.getFirst().getId());
    }



    @Test
    void shouldReturnEmptyUserDrugMonitorWhenNoMatchingDrugs() {
        User user = User.builder().id("1").build();

        when(principalSupplier.get()).thenReturn(user);
        when(userDrugRepository.findAllByUserId("1")).thenReturn(Collections.emptyList());

        List<UserDrugMonitorResponse> result = drugsService.getUserDrugMonitor();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}
