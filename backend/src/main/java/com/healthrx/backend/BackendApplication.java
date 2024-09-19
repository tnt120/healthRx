package com.healthrx.backend;

import com.healthrx.backend.api.internal.City;
import com.healthrx.backend.api.internal.Parameter;
import com.healthrx.backend.api.internal.Specialization;
import com.healthrx.backend.api.internal.Unit;
import com.healthrx.backend.repository.CityRepository;
import com.healthrx.backend.repository.ParameterRepository;
import com.healthrx.backend.repository.SpecializationRepository;
import com.healthrx.backend.repository.UnitRepository;
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
            final CityRepository cityRepository
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
//            parameterRepository.save(new Parameter().setName("Waga").setUnit(unit1).setMinValue("18.5").setMaxValue("24.9").setHint("Podana w kg"));
//            parameterRepository.save(new Parameter().setName("Sen").setUnit(unit2).setMinValue("7").setMaxValue("9").setHint("W godzinach snu"));
//            parameterRepository.save(new Parameter()
//                    .setName("Tętno")
//                    .setUnit(unit3)
//                    .setMinValue("60")
//                    .setMaxValue("100")
//                    .setHint("BPM - uderzenia na minutę"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Poziom cukru we krwi")
//                    .setUnit(unit4)
//                    .setMinValue("70")
//                    .setMaxValue("140")
//                    .setHint("Podana w mg/dL"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Ciśnienie krwi")
//                    .setUnit(unit5)
//                    .setMinValue("90/60")
//                    .setMaxValue("120/80")
//                    .setHint("Podana w mmHg"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Saturacja")
//                    .setUnit(unit6)
//                    .setMinValue("95")
//                    .setMaxValue("100")
//                    .setHint("Podana w procentach"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Spożycie wody")
//                    .setUnit(unit7)
//                    .setMinValue("2000")
//                    .setMaxValue("3000")
//                    .setHint("Podana w ml"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Kroki")
//                    .setUnit(unit8)
//                    .setMinValue("5000")
//                    .setMaxValue("10000")
//                    .setHint("Liczba kroków"));
//
//            parameterRepository.save(new Parameter()
//                    .setName("Poziom stresu")
//                    .setUnit(unit9)
//                    .setMinValue("1")
//                    .setMaxValue("10")
//                    .setHint("Poziom stresu w skali od 1 do 10"));
//
//            cityRepository.save(new City().setName("Kraków"));
//            cityRepository.save(new City().setName("Warszawa"));
//            cityRepository.save(new City().setName("Wrocław"));
        };
    }
}
