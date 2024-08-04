package com.healthrx.backend.batch;

import com.healthrx.backend.api.internal.Drug;
import com.healthrx.backend.api.internal.DrugPack;
import com.healthrx.backend.batch.utils.DrugXml;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;

public class DrugItemProcessor implements ItemProcessor<DrugXml, Drug> {

    @Override
    public Drug process(DrugXml drugXml) throws Exception {
        Drug drug = new Drug();
        drug.setId(drugXml.getId());
        drug.setName(drugXml.getName());
        drug.setPower(drugXml.getPower());
        drug.setPharmaceuticalFormName(truncate(drugXml.getPharmaceuticalFormName(), 512));
        drug.setCompany(drugXml.getCompany());
        drug.setProcedureType(drugXml.getProcedureType());
        drug.setPermitNumber(drugXml.getPermitNumber());
        drug.setPermitExpiration(drugXml.getPermitExpiration());

        if (drugXml.getInfo() != null && drugXml.getInfo().length() <= 1024) {
            drug.setInfo(drugXml.getInfo());
        }

        if (drugXml.getCharacteristic() != null && drugXml.getCharacteristic().length() <= 1024) {
            drug.setCharacteristic(drugXml.getCharacteristic());
        }

        if (drugXml.getAtcCodes() != null && drugXml.getAtcCodes().getCodes() != null) {
            String atcCode = String.join(",", drugXml.getAtcCodes().getCodes());
            if (!atcCode.isEmpty()) {
                drug.setAtcCodes(atcCode);
            }
        }

        List<DrugPack> drugPacks = new ArrayList<>();
        if (drugXml.getDrugPacks() != null) {
            for (DrugXml.DrugPackXml packXml : drugXml.getDrugPacks()) {
                DrugPack pack = new DrugPack();
                pack.setId(packXml.getId());
                pack.setGtinCode(packXml.getGtinCode());
                pack.setAccessibilityCategory(packXml.getAccessibilityCategory());

                if (packXml.getPackageUnits() != null && !packXml.getPackageUnits().isEmpty()) {
                    DrugXml.DrugPackXml.PackageUnit unit = packXml.getPackageUnits().getFirst();
                    pack.setPackagesQuantity(unit.getPackagesQuantity());
                    pack.setPackUnit(unit.getPackUnit());
                    pack.setPackSize(unit.getPackSize());
                    pack.setPackType(unit.getPackType());
                }

                pack.setDrug(drug);
                drugPacks.add(pack);
            }
        }

        drug.setDrugPacks(drugPacks);
        return drug;
    }

    private String truncate(String value, int length) {
        return value != null && value.length() > length ? value.substring(0, length) : value;
    }
}
