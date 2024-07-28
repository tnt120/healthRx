package com.healthrx.backend;

import com.healthrx.backend.api.internal.Parameter;
import com.healthrx.backend.api.internal.Specialization;
import com.healthrx.backend.api.internal.Unit;
import com.healthrx.backend.repository.ParameterRepository;
import com.healthrx.backend.repository.SpecializationRepository;
import com.healthrx.backend.repository.UnitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(
            final SpecializationRepository specializationRepository,
            final UnitRepository unitRepository,
            final ParameterRepository parameterRepository
            ) {
        return args -> {
            specializationRepository.save(new Specialization().setName("Kardiolog"));
            specializationRepository.save(new Specialization().setName("Ortopeda"));
            specializationRepository.save(new Specialization().setName("Pediatra"));
            Unit unit1 = unitRepository.save(new Unit().setName("Kilogram").setSymbol("kg"));
            Unit unit2 = unitRepository.save(new Unit().setName("Godzina").setSymbol("h"));

            parameterRepository.save(new Parameter().setName("Waga").setUnit(unit1).setMinValue("18.5").setMaxValue("24.9"));
            parameterRepository.save(new Parameter().setName("Sen").setUnit(unit2).setMinValue("7").setMaxValue("9"));
        };
    }
}
