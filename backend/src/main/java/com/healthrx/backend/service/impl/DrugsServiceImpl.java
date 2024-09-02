package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.api.internal.enums.Days;
import com.healthrx.backend.mapper.DrugMapper;
import com.healthrx.backend.mapper.UserDrugMapper;
import com.healthrx.backend.repository.*;
import com.healthrx.backend.service.DrugsService;
import com.healthrx.backend.specification.DrugSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
public class DrugsServiceImpl implements DrugsService {
    private final DrugRepository drugRepository;
    private final DrugPackRepository drugPackRepository;
    private final UserDrugRepository userDrugRepository;
    private final DrugDoseDayRepository drugDoseDayRepository;
    private final DrugDoseTimeRepository drugDoseTimeRepository;
    private final DrugLogRepository drugLogRepository;
    private final DrugMapper drugMapper;
    private final UserDrugMapper userDrugMapper;
    private final Supplier<User> principalSupplier;

    @Override
    public PageResponse<DrugResponse> getAllDrugs(Integer page, Integer size, String sortBy, String order, String name) {
        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Drug> spec = Specification.where(null);
        if (name != null) spec = spec.and(DrugSpecification.nameContains(name));

        Page<Drug> drugs = drugRepository.findAll(spec, pageable);

        List<DrugResponse> drugResponse = drugs.stream()
                .map(drug -> {
                    String drugPack = drugPackRepository.findPackUnitByDrugId(drug.getId()).getFirst();

                    return drugMapper.map(drug, drugPack);
                })
                .toList();

        return new PageResponse<DrugResponse>()
                .setContent(drugResponse)
                .setCurrentPage(drugs.getNumber())
                .setPageSize(drugs.getSize())
                .setTotalElements(drugs.getTotalElements())
                .setLast(drugs.isLast())
                .setFirst(drugs.isFirst());
    }

    @Override
    public PageResponse<UserDrugsResponse> getUserDrugs(Integer page, Integer size, String sortBy, String order) {

        User user = principalSupplier.get();

        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserDrug> userDrugs = userDrugRepository.findByUser(user, pageable);

        List<UserDrugsResponse> userDrugsResponses = userDrugs.stream()
                .map(userDrug -> userDrugMapper.map(
                        userDrug,
                        drugPackRepository.findPackUnitByDrugId(userDrug.getDrug().getId()).getFirst()
                ))
                .toList();


        return new PageResponse<UserDrugsResponse>()
                .setContent(userDrugsResponses)
                .setCurrentPage(userDrugs.getNumber())
                .setPageSize(userDrugs.getSize())
                .setTotalElements(userDrugs.getTotalElements())
                .setTotalPages(userDrugs.getTotalPages())
                .setLast(userDrugs.isLast())
                .setFirst(userDrugs.isFirst());
    }

    @Override
    public List<UserDrugMonitorResponse> getUserDrugMonitor() {

        User user = principalSupplier.get();

        Days today = Days.from(LocalDate.now().getDayOfWeek());

        return userDrugRepository.findAllByUserId(user.getId()).stream()
                .filter(userDrug -> userDrug.getDrugDoseDays().stream().map(DrugDoseDay::getDay).toList().contains(today))
                .flatMap(userDrug -> userDrug.getDrugDoseTimes().stream()
                        .map(time -> {
                            LocalTime takenTime = drugLogRepository.findDrugLogByDrugIdAndUserIdAndTimeToday(
                                    userDrug.getDrug().getId(),
                                    user.getId(),
                                    time.getDoseTime()
                            ).stream().map(DrugLog::getTakenTime).findFirst().orElse(null);

                            return drugMapper.map(userDrug, drugPackRepository.findPackUnitByDrugId(userDrug.getDrug().getId()).getFirst(), time.getDoseTime(), takenTime);
                        })
                ).toList();
    }

    @Override
    @Transactional
    public UserDrugMonitorResponse setUserDrugMonitor(UserDrugMonitorRequest request) {
        User user = principalSupplier.get();

        UserDrug userDrug = userDrugRepository.findById(request.getId())
                .orElseThrow(USER_DRUG_NOT_FOUND::getError);

        if (!userDrug.getUser().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        Days today = Days.from(LocalDate.now().getDayOfWeek());

        if (
                !userDrug.getDrugDoseTimes().stream().map(DrugDoseTime::getDoseTime).toList().contains(request.getTime()) ||
                !userDrug.getDrugDoseDays().stream().map(DrugDoseDay::getDay).toList().contains(request.getDay()) ||
                !userDrug.getDrug().getId().equals(request.getDrugId()) ||
                !request.getDay().equals(today)
        ) {
            throw WRONG_DRUG_MONITOR_DATA.getError();
        }

        DrugLog drugLog = drugLogRepository.findDrugLogByDrugIdAndUserIdAndTimeToday(request.getDrugId(), user.getId(), request.getTime())
                .orElse(null);

        if (drugLog != null) {
            throw DRUG_LOG_ALREADY_EXISTS.getError();
        }

        this.drugLogRepository.save(DrugLog.builder()
                .user(user)
                .drug(userDrug.getDrug())
                .day(request.getDay())
                .time(request.getTime())
                .takenTime(request.getTakenTime())
                .build());

        return this.drugMapper.map(userDrug, drugPackRepository.findPackUnitByDrugId(userDrug.getDrug().getId()).getFirst(), request.getTime(), request.getTakenTime());
    }

    @Override
    @Transactional
    public UserDrugMonitorResponse editUserDrugMonitor(UserDrugMonitorRequest request) {
        User user = principalSupplier.get();

        DrugLog drugLog = drugLogRepository.findDrugLogByDrugIdAndUserIdAndTimeToday(request.getDrugId(), user.getId(), request.getTime())
                .orElseThrow(DRUG_LOG_NOT_FOUND::getError);

        Days today = Days.from(LocalDate.now().getDayOfWeek());

        if (!drugLog.getDay().equals(request.getDay()) || !request.getDay().equals(today)) {
            throw WRONG_DRUG_MONITOR_DATA.getError();
        }

        drugLog.setTakenTime(request.getTakenTime());

        drugLogRepository.save(drugLog);

        UserDrug userDrug = userDrugRepository.findById(request.getId())
                .orElseThrow(USER_DRUG_NOT_FOUND::getError);

        if (!userDrug.getDrug().getId().equals(request.getDrugId())) {
            throw WRONG_DRUG_MONITOR_DATA.getError();
        }

        return drugMapper.map(userDrug, drugPackRepository.findPackUnitByDrugId(userDrug.getDrug().getId()).getFirst(), request.getTime(), request.getTakenTime());
    }

    @Override
    public Void deleteUserDrugMonitor(Integer drugId, String time) {
        User user = principalSupplier.get();

        DrugLog drugLog = drugLogRepository.findDrugLogByDrugIdAndUserIdAndTimeToday(drugId, user.getId(), LocalTime.parse(time))
                .orElseThrow(DRUG_LOG_NOT_FOUND::getError);

        drugLogRepository.delete(drugLog);

        return null;
    }

    @Override
    @Transactional
    public UserDrugsResponse addUserDrug(UserDrugsRequest request) {

        User user = principalSupplier.get();

        Drug drug = drugRepository.findById(request.getDrugId())
                .orElseThrow(DRUG_NOT_FOUND::getError);

        userDrugRepository.findUserDrugByUserIdAndDrugId(drug.getId(), user.getId())
                .ifPresent(userDrug -> {
                    throw USER_DRUG_ALREADY_EXISTS.getError();
                });

        UserDrug userDrug = userDrugMapper.map(request, drug, user);

        userDrugRepository.save(userDrug);

        List<DrugDoseDay> drugDoseDays = addAllDoseEntities(
                userDrug,
                request.getDoseDays(),
                (ud, day) -> DrugDoseDay.builder().day(day).userDrugs(ud).build(),
                drugDoseDayRepository
        );

        List<DrugDoseTime> drugDoseTimes = addAllDoseEntities(
                userDrug,
                request.getDoseTimes(),
                (ud, time) -> DrugDoseTime.builder().doseTime(time).userDrugs(ud).build(),
                drugDoseTimeRepository
        );

        userDrug.setDrugDoseDays(drugDoseDays);
        userDrug.setDrugDoseTimes(drugDoseTimes);

        return userDrugMapper.map(userDrug, drugPackRepository.findPackUnitByDrugId(drug.getId()).getFirst());
    }

    @Override
    @Transactional
    public UserDrugsResponse editUserDrug(UserDrugsRequest request, String userDrugId) {

        User user = principalSupplier.get();

        UserDrug userDrug = userDrugRepository.findById(userDrugId)
                .orElseThrow(USER_DRUG_NOT_FOUND::getError);

        if (!userDrug.getUser().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        Optional.ofNullable(request.getAmount())
                .ifPresent(userDrug::setAmount);

        Optional.ofNullable(request.getStartDate())
                .ifPresent(userDrug::setStartDate);

        userDrug.setEndDate(request.getEndDate());

        Optional.ofNullable(request.getPriority())
                .ifPresent(userDrug::setPriority);

        Optional.ofNullable(request.getDoseSize())
                .ifPresent(userDrug::setDoseSize);

        Optional.ofNullable(request.getDoseTimes())
                .ifPresent(doseTimes -> {
                    drugDoseTimeRepository.deleteAll(userDrug.getDrugDoseTimes());

                    List<DrugDoseTime> drugDoseTimes = addAllDoseEntities(
                            userDrug,
                            request.getDoseTimes(),
                            (ud, time) -> DrugDoseTime.builder().doseTime(time).userDrugs(ud).build(),
                            drugDoseTimeRepository
                    );

                    userDrug.setDrugDoseTimes(drugDoseTimes);
                });

        Optional.ofNullable(request.getDoseDays())
                .ifPresent(doseDays -> {
                    drugDoseDayRepository.deleteAll(userDrug.getDrugDoseDays());

                    List<DrugDoseDay> drugDoseDays = addAllDoseEntities(
                            userDrug,
                            request.getDoseDays(),
                            (ud, day) -> DrugDoseDay.builder().day(day).userDrugs(ud).build(),
                            drugDoseDayRepository
                    );

                    userDrug.setDrugDoseDays(drugDoseDays);
                });

        userDrugRepository.save(userDrug);

        return userDrugMapper.map(userDrug, drugPackRepository.findPackUnitByDrugId(userDrug.getDrug().getId()).getFirst());
    }

    @Override
    public Void deleteUserDrug(String id) {

        User user = principalSupplier.get();

        UserDrug userDrug = userDrugRepository.findById(id)
                .orElseThrow(DRUG_NOT_FOUND::getError);

        if (!userDrug.getUser().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        userDrugRepository.delete(userDrug);
        return null;
    }

    private <T, U> List<T> addAllDoseEntities(UserDrug userDrug, List<U> doseValues, BiFunction<UserDrug, U, T> entityBuilder, JpaRepository<T, String> repository) {
        List<T> doseEntities = new ArrayList<>();

        doseValues.forEach(value -> {
            T doseEntity = entityBuilder.apply(userDrug, value);
            doseEntities.add(doseEntity);
            repository.save(doseEntity);
        });

        return doseEntities;
    }
}
