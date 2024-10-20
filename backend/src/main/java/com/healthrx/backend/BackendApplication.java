package com.healthrx.backend;

import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(
            final SpecializationRepository specializationRepository,
            final UnitRepository unitRepository,
            final ParameterRepository parameterRepository,
            final CityRepository cityRepository,
            final ActivityRepository activityRepository
            ) {
        return args -> {
//            specializationRepository.save(new Specialization().setName("Kardiolog"));
//            specializationRepository.save(new Specialization().setName("Ortopeda"));
//            specializationRepository.save(new Specialization().setName("Pediatra"));
//            Unit unit1 = unitRepository.save(new Unit().setName("Kilogram").setSymbol("kg"));
//            Unit unit2 = unitRepository.save(new Unit().setName("Godzina").setSymbol("h"));
//            Unit unit3 = unitRepository.save(new Unit().setName("BPM").setSymbol("bpm"));
//            Unit unit4 = unitRepository.save(new Unit().setName("mg/dL").setSymbol("mg/dL"));
//            Unit unit5 = unitRepository.save(new Unit().setName("mmHg").setSymbol("mmHg"));
//            Unit unit6 = unitRepository.save(new Unit().setName("Percent").setSymbol("%"));
//            Unit unit7 = unitRepository.save(new Unit().setName("ml").setSymbol("ml"));
//            Unit unit8 = unitRepository.save(new Unit().setName("Steps").setSymbol("steps"));
//            Unit unit9 = unitRepository.save(new Unit().setName("Stress Level").setSymbol("Level"));
//
//            parameterRepository.save(new Parameter().setName("Waga").setUnit(unit1).setMinValue(1.0).setMaxValue(400.0).setMinStandardValue(18.5).setMaxStandardValue(24.99).setHint("Podana w kg"));
//            parameterRepository.save(new Parameter().setName("Sen").setUnit(unit2).setMinValue(1.0).setMaxValue(48.0).setMinStandardValue(7.0).setMaxStandardValue(9.0).setHint("W godzinach snu"));
//            parameterRepository.save(new Parameter()
//                    .setName("Tętno")
//                    .setUnit(unit3)
//                    .setMinValue(30.0)
//                    .setMaxValue(200.0)
//                    .setMinStandardValue(60.0)
//                    .setMaxStandardValue(100.0)
//                    .setHint("BPM - uderzenia na minutę"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Poziom cukru we krwi")
//                    .setUnit(unit4)
//                    .setMinValue(0.0)
//                    .setMaxValue(1000.0)
//                    .setMinStandardValue(70.0)
//                    .setMaxStandardValue(140.0)
//                    .setHint("Podana w mg/dL"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Ciśnienie skurczowe krwi")
//                    .setUnit(unit5)
//                    .setMinValue(20.0)
//                    .setMaxValue(300.0)
//                    .setMinStandardValue(100.0)
//                    .setMaxStandardValue(139.0)
//                    .setHint("Podana w mmHg"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Ciśnienie rozkurczowe krwi")
//                    .setUnit(unit5)
//                    .setMinValue(10.0)
//                    .setMaxValue(300.0)
//                    .setMinStandardValue(60.0)
//                    .setMaxStandardValue(89.0)
//                    .setHint("Podana w mmHg"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Saturacja")
//                    .setUnit(unit6)
//                    .setMinValue(0.0)
//                    .setMaxValue(100.0)
//                    .setMinStandardValue(95.0)
//                    .setMaxStandardValue(100.0)
//                    .setHint("Podana w procentach"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Spożycie wody")
//                    .setUnit(unit7)
//                    .setMinValue(0.0)
//                    .setMaxValue(10000.0)
//                    .setMinStandardValue(2000.0)
//                    .setMaxStandardValue(8000.0)
//                    .setHint("Podana w ml"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Kroki")
//                    .setUnit(unit8)
//                    .setMinValue(0.0)
//                    .setMaxValue(100000.0)
//                    .setMinStandardValue(5000.0)
//                    .setMaxStandardValue(50000.0)
//                    .setHint("Liczba kroków"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Poziom stresu")
//                    .setUnit(unit9)
//                    .setMinValue(1.0)
//                    .setMaxValue(10.0)
//                    .setMinStandardValue(1.0)
//                    .setMaxStandardValue(4.0)
//                    .setHint("Poziom stresu w skali od 1 do 10"));
//
//            cityRepository.save(new City().setName("Kraków"));
//            cityRepository.save(new City().setName("Warszawa"));
//            cityRepository.save(new City().setName("Wrocław"));

//            activityRepository.save(Activity.builder().name("Piłka nożna").isPopular(true).metFactor(9.0).build());
//            activityRepository.save(Activity.builder().name("Koszykówka").isPopular(true).metFactor(8.0).build());
//            activityRepository.save(Activity.builder().name("Siatkówka").isPopular(true).metFactor(8.0).build());
//            activityRepository.save(Activity.builder().name("Bieganie (8 km/h)").isPopular(true).metFactor(8.0).build());
//            activityRepository.save(Activity.builder().name("Bieganie (12 km/h)").isPopular(true).metFactor(12.0).build());
//            activityRepository.save(Activity.builder().name("Skakanka wolno").isPopular(false).metFactor(8.0).build());
//            activityRepository.save(Activity.builder().name("Skakanka szybko").isPopular(false).metFactor(12.0).build());
//            activityRepository.save(Activity.builder().name("Tenis").isPopular(true).metFactor(7.0).build());
//            activityRepository.save(Activity.builder().name("Tenis stołowy").isPopular(true).metFactor(4.0).build());
//            activityRepository.save(Activity.builder().name("Golf").isPopular(false).metFactor(4.4).build());
//            activityRepository.save(Activity.builder().name("Pływanie rekreacyjne").isPopular(true).metFactor(4.5).build());
//            activityRepository.save(Activity.builder().name("Pływanie szybkie").isPopular(false).metFactor(10.0).build());
        };
    }
}
