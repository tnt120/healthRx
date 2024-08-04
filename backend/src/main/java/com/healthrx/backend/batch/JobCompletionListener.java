package com.healthrx.backend.batch;

import com.healthrx.backend.api.internal.Drug;
import com.healthrx.backend.api.internal.DrugPack;
import com.healthrx.backend.repository.DrugPackRepository;
import com.healthrx.backend.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCompletionListener implements JobExecutionListener {

    private final DrugRepository drugRepository;
    private final DrugPackRepository drugPackRepository;
    private static final String PROCESSED_DRUG_IDS_KEY = "processedDrugIds";
    private static final String PROCESSED_DRUG_PACK_IDS_KEY = "processedDrugPackIds";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job listener beforeJob: {}", jobExecution);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterJob(JobExecution jobExecution) {
        Set<Integer> processedDrugIds = (Set<Integer>) jobExecution.getExecutionContext()
                .get(PROCESSED_DRUG_IDS_KEY);

        if (processedDrugIds != null) {
            Set<Integer> existingDrugIds = drugRepository.findAll().stream()
                    .map(Drug::getId).collect(Collectors.toSet());
            existingDrugIds.removeAll(processedDrugIds);
            for (Integer drugId : existingDrugIds) {
                drugRepository.deleteById(drugId);
            }
        }

        Set<Integer> processedDrugPackIds = (Set<Integer>) jobExecution.getExecutionContext()
                .get(PROCESSED_DRUG_PACK_IDS_KEY);

        if (processedDrugPackIds != null) {
            Set<Integer> existingDrugPackIds = drugPackRepository.findAll().stream()
                    .map(DrugPack::getId).collect(Collectors.toSet());
            existingDrugPackIds.removeAll(processedDrugPackIds);
            for (Integer drugPackId : existingDrugPackIds) {
                drugPackRepository.deleteById(drugPackId);
            }
        }
    }
}
