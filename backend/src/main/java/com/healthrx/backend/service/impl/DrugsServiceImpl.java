package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.DrugResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.UserDrugsRequest;
import com.healthrx.backend.api.external.UserDrugsResponse;
import com.healthrx.backend.api.internal.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
public class DrugsServiceImpl implements DrugsService {
    private final DrugRepository drugRepository;
    private final DrugPackRepository drugPackRepository;
    private final UserDrugRepository userDrugRepository;
    private final DrugDoseDayRepository drugDoseDayRepository;
    private final DrugDoseTimeRepository drugDoseTimeRepository;
    private final DrugMapper drugMapper;
    private final UserDrugMapper userDrugMapper;

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
    @Transactional
    public UserDrugsResponse addUserDrug(UserDrugsRequest request) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Drug drug = drugRepository.findById(request.getDrugId())
                .orElseThrow(DRUG_NOT_FOUND::getError);

        userDrugRepository.findUserDrugByUserIdAndDrugId(drug.getId(), user.getId())
                .ifPresent(userDrug -> {
                    throw USER_DRUG_ALREADY_EXISTS.getError();
                });

        UserDrug userDrug = userDrugMapper.map(request, drug, user);

        userDrugRepository.save(userDrug);

        List<DrugDoseDay> drugDoseDays = new ArrayList<>();

        request.getDoseDays().forEach(day -> {
            DrugDoseDay drugDoseDay = DrugDoseDay.builder()
                    .day(day)
                    .userDrugs(userDrug)
                    .build();

            drugDoseDays.add(drugDoseDay);

            drugDoseDayRepository.save(drugDoseDay);
        });

        List<DrugDoseTime> drugDoseTimes = new ArrayList<>();

        request.getDoseTimes().forEach(time -> {
            DrugDoseTime drugDoseTime = DrugDoseTime.builder()
                    .doseTime(time)
                    .userDrugs(userDrug)
                    .build();

            drugDoseTimes.add(drugDoseTime);

            drugDoseTimeRepository.save(drugDoseTime);
        });

        userDrug.setDrugDoseDays(drugDoseDays);
        userDrug.setDrugDoseTimes(drugDoseTimes);

        return userDrugMapper.map(userDrug, drugPackRepository.findPackUnitByDrugId(drug.getId()).getFirst());
    }

    @Override
    public Void deleteUserDrug(String id) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDrug userDrug = userDrugRepository.findById(id)
                .orElseThrow(DRUG_NOT_FOUND::getError);

        if (!userDrug.getUser().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        userDrugRepository.delete(userDrug);
        return null;
    }
}
