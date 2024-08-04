package com.healthrx.backend.batch.utils;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "opakowanie")
@Getter
@Setter
public class DrugPackXml {

    @XmlElement(name = "id")
    private Integer id;

    @XmlElement(name = "kodGTIN")
    private String gtinCode;

    @XmlElement(name = "rodzajOpakowania")
    private String packType;

    @XmlElement(name = "pojemnosc")
    private String packSize;

    @XmlElement(name = "liczbaOpakowan")
    private String packagesQuantity;

    @XmlElement(name = "jednostkaPojemnosci")
    private String packUnit;
}