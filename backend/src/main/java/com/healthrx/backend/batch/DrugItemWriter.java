//package com.healthrx.backend.batch;
//
//import com.healthrx.backend.api.internal.Drug;
//import com.healthrx.backend.api.internal.DrugPack;
//import com.healthrx.backend.repository.DrugPackRepository;
//import com.healthrx.backend.repository.DrugRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.item.Chunk;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@RequiredArgsConstructor
//public class DrugItemWriter implements ItemWriter<Drug> {
//
//    private final DrugRepository drugRepository;
//    private final DrugPackRepository drugPackRepository;
//
//    @Override
//    public void write(Chunk<? extends Drug> drugs) throws Exception {
//        Set<Integer> existingDrugIds = drugRepository.findAll().stream()
//                .map(Drug::getId)
//                .collect(Collectors.toSet());
//
//        for (Drug drug : drugs.getItems()) {
//            existingDrugIds.remove(drug.getId());
//
//            Optional<Drug> optionalDrug = drugRepository.findById(drug.getId());
//
//            if (optionalDrug.isPresent()) {
//                Drug existingDrug = optionalDrug.get();
//
//                if (existingDrug.equals(drug)) {
//                    continue;
//                }
//
//                updateDrug(existingDrug, drug);
//            } else {
//                drugRepository.save(drug);
//
//                for (DrugPack drugPack : drug.getDrugPacks()) {
//                    drugPack.setDrug(drug);
//                    drugPackRepository.save(drugPack);
//                }
//            }
//        }
//
//        for (Integer drugId : existingDrugIds) {
//            drugRepository.deleteById(drugId);
//        }
//    }
//
//    private void updateDrug(Drug existingDrug, Drug newDrug) {
//        existingDrug.setName(newDrug.getName());
//        existingDrug.setPower(newDrug.getPower());
//        existingDrug.setPharmaceuticalFormName(newDrug.getPharmaceuticalFormName());
//        existingDrug.setCompany(newDrug.getCompany());
//        existingDrug.setProcedureType(newDrug.getProcedureType());
//        existingDrug.setPermitNumber(newDrug.getPermitNumber());
//        existingDrug.setPermitExpiration(newDrug.getPermitExpiration());
//        existingDrug.setInfo(newDrug.getInfo());
//        existingDrug.setCharacteristic(newDrug.getCharacteristic());
//        existingDrug.setAtcCode(newDrug.getAtcCode());
//
//        for (DrugPack drugPack : newDrug.getDrugPacks()) {
//            Optional<DrugPack> optionalDrugPack = drugPackRepository.findById(drugPack.getId());
//
//            if (optionalDrugPack.isPresent()) {
//                DrugPack existingDrugPack = optionalDrugPack.get();
//                existingDrugPack.setGtinCode(drugPack.getGtinCode());
//                existingDrugPack.setPackType(drugPack.getPackType());
//                existingDrugPack.setPackSize(drugPack.getPackSize());
//                existingDrugPack.setPackagesQuantity(drugPack.getPackagesQuantity());
//                existingDrugPack.setDrug(existingDrug);
//                existingDrugPack.setPackUnit(drugPack.getPackUnit());
//                drugPackRepository.save(existingDrugPack);
//            } else {
//                drugPack.setDrug(existingDrug);
//                drugPackRepository.save(drugPack);
//            }
//        }
//
//        drugRepository.save(existingDrug);
//    }
//}
