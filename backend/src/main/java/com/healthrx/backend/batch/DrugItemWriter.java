package com.healthrx.backend.batch;

import com.healthrx.backend.api.internal.Drug;
import com.healthrx.backend.api.internal.DrugPack;
import com.healthrx.backend.repository.DrugPackRepository;
import com.healthrx.backend.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DrugItemWriter implements ItemWriter<Drug> {

    private static final String PROCESSED_DRUG_IDS_KEY = "processedDrugIds";
    private static final String PROCESSED_DRUG_PACK_IDS_KEY = "processedDrugPackIds";

    private final DrugRepository drugRepository;
    private final DrugPackRepository drugPackRepository;
    private Set<Integer> processedDrugIds;
    private Set<Integer> processedDrugPackIds;

    @SuppressWarnings("unchecked")
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        processedDrugIds = (Set<Integer>) stepExecution.getJobExecution().getExecutionContext()
                .get(PROCESSED_DRUG_IDS_KEY);

        if (processedDrugIds == null) {
            processedDrugIds = new HashSet<>();
            stepExecution.getJobExecution().getExecutionContext()
                    .put(PROCESSED_DRUG_IDS_KEY, processedDrugIds);
        }

        processedDrugPackIds = (Set<Integer>) stepExecution.getJobExecution().getExecutionContext()
                .get(PROCESSED_DRUG_PACK_IDS_KEY);

        if (processedDrugPackIds == null) {
            processedDrugPackIds = new HashSet<>();
            stepExecution.getJobExecution().getExecutionContext()
                    .put(PROCESSED_DRUG_PACK_IDS_KEY, processedDrugPackIds);
        }
    }

    @Override
    public synchronized void write(Chunk<? extends Drug> items) throws Exception {

        for (Drug newDrug : items) {
            processedDrugIds.add(newDrug.getId());
            processedDrugPackIds.addAll(newDrug.getDrugPacks().stream()
                    .map(DrugPack::getId)
                    .collect(Collectors.toSet()));

            Drug existingDrug = drugRepository.findById(newDrug.getId()).orElse(null);

            if (existingDrug == null) {
                drugRepository.save(newDrug);

                if (newDrug.getDrugPacks() != null) {
                    for (DrugPack pack : newDrug.getDrugPacks()) {
                        pack.setDrug(newDrug);
                        drugPackRepository.save(pack);
                    }
                }

            } else if (!existingDrug.equals(newDrug)) {
                existingDrug.setName(newDrug.getName());
                existingDrug.setPower(newDrug.getPower());
                existingDrug.setPharmaceuticalFormName(newDrug.getPharmaceuticalFormName());
                existingDrug.setCompany(newDrug.getCompany());
                existingDrug.setProcedureType(newDrug.getProcedureType());
                existingDrug.setPermitNumber(newDrug.getPermitNumber());
                existingDrug.setPermitExpiration(newDrug.getPermitExpiration());
                existingDrug.setInfo(newDrug.getInfo());
                existingDrug.setCharacteristic(newDrug.getCharacteristic());
                existingDrug.setAtcCodes(newDrug.getAtcCodes());

                drugRepository.save(existingDrug);

                updateDrugPacks(existingDrug, newDrug.getDrugPacks());
            }
        }
    }

    private void updateDrugPacks(Drug existingDrug, List<DrugPack> newDrugPacks) {
        Set<Integer> existingPackIds = existingDrug.getDrugPacks().stream()
                .map(DrugPack::getId)
                .collect(Collectors.toSet());

        for (DrugPack newPack : newDrugPacks) {
            if (existingPackIds.contains(newPack.getId())) {
                DrugPack existingPack = drugPackRepository.findById(newPack.getId()).orElse(null);

                if (existingPack != null && !existingPack.equals(newPack)) {
                    existingPack.setGtinCode(newPack.getGtinCode());
                    existingPack.setPackType(newPack.getPackType());
                    existingPack.setPackSize(newPack.getPackSize());
                    existingPack.setPackagesQuantity(newPack.getPackagesQuantity());
                    existingPack.setPackUnit(newPack.getPackUnit());
                    existingPack.setAccessibilityCategory(newPack.getAccessibilityCategory());

                    drugPackRepository.save(existingPack);
                }
            } else {
                newPack.setDrug(existingDrug);
                drugPackRepository.save(newPack);
            }
        }

        Set<Integer> newPackIds = newDrugPacks.stream()
                .map(DrugPack::getId)
                .collect(Collectors.toSet());

        existingDrug.getDrugPacks().removeIf(pack -> !newPackIds.contains(pack.getId()));
    }
}
