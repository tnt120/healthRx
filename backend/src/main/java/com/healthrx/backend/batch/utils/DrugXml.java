package com.healthrx.backend.batch.utils;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "produktLeczniczy", namespace = "http://rejestrymedyczne.ezdrowie.gov.pl/rpl/eksport-danych-v1.0")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DrugXml {

    @XmlAttribute(name = "id")
    private Integer id;

    @XmlAttribute(name = "nazwaProduktu")
    private String name;

    @XmlAttribute(name = "moc")
    private String power;

    @XmlAttribute(name = "nazwaPostaciFarmaceutycznej")
    private String pharmaceuticalFormName;

    @XmlAttribute(name = "podmiotOdpowiedzialny")
    private String company;

    @XmlAttribute(name = "typProcedury")
    private String procedureType;

    @XmlAttribute(name = "numerPozwolenia")
    private String permitNumber;

    @XmlAttribute(name = "waznoscPozwolenia")
    private String permitExpiration;

    @XmlAttribute(name = "ulotka")
    private String info;

    @XmlAttribute(name = "charakterystyka")
    private String characteristic;

//    @XmlElement(name = "kodyATC", namespace = "http://rejestrymedyczne.ezdrowie.gov.pl/rpl/eksport-danych-v1.0")
//    private AtcCodes atcCodes;
//
//    @XmlAccessorType(XmlAccessType.FIELD)
//    @XmlRootElement(name = "kodyATC", namespace = "http://rejestrymedyczne.ezdrowie.gov.pl/rpl/eksport-danych-v1.0")
//    @Data
//    public static class AtcCodes {
//        @XmlElement(name = "kodATC", namespace = "http://rejestrymedyczne.ezdrowie.gov.pl/rpl/eksport-danych-v1.0")
//        private List<String> codes;
//    }
}