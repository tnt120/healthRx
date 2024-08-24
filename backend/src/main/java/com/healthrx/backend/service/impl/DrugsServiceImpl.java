package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.DrugResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.internal.Drug;
import com.healthrx.backend.mapper.DrugMapper;
import com.healthrx.backend.repository.DrugPackRepository;
import com.healthrx.backend.repository.DrugRepository;
import com.healthrx.backend.service.DrugsService;
import com.healthrx.backend.specification.DrugSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrugsServiceImpl implements DrugsService {
    private final DrugRepository drugRepository;
    private final DrugPackRepository drugPackRepository;
    private final DrugMapper drugMapper;

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
}
