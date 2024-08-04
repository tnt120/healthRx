//package com.healthrx.backend.batch;
//
//import com.healthrx.backend.api.internal.Drug;
//import com.healthrx.backend.api.internal.DrugPack;
//import com.healthrx.backend.batch.utils.DrugXml;
//import org.springframework.batch.item.ItemProcessor;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class DrugItemProcessor implements ItemProcessor<DrugXml, Drug> {
//
//    @Override
//    public Drug process(DrugXml drugXml) throws Exception {
//        Drug drug = new Drug();
//        drug.setId(drugXml.getId());
//        drug.setName(drugXml.getName());
//        drug.setPower(drugXml.getPower());
//        drug.setPharmaceuticalFormName(drugXml.getPharmaceuticalFormName());
//        drug.setCompany(drugXml.getCompany());
//        drug.setProcedureType(drugXml.getProcedureType());
//        drug.setPermitNumber(drugXml.getPermitNumber());
//        drug.setPermitExpiration(drugXml.getPermitExpiration());
//        drug.setInfo(drugXml.getInfo());
//        drug.setCharacteristic(drugXml.getCharacteristic());
//        drug.setAtcCode(drugXml.getAtcCode().getFirst());
//
//        List<DrugPack> drugPacks = drugXml.getDrugPacks().stream()
//                .map(drugPackXml -> DrugPack.builder()
//                        .gtinCode(drugPackXml.getGtinCode())
//                        .packType(drugPackXml.getPackType())
//                        .packSize(drugPackXml.getPackSize())
//                        .packagesQuantity(drugPackXml.getPackagesQuantity())
//                        .packUnit(drugPackXml.getPackUnit())
//                        .build())
//                .collect(Collectors.toList());
//
//        drug.setDrugPacks(drugPacks);
//
//        return drug;
//    }
//}
