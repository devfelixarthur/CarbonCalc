package br.com.actionlabs.carboncalc.repository;

import br.com.actionlabs.carboncalc.model.CalculationCarbon;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CalculationCarbonRepository extends MongoRepository<CalculationCarbon, String> {

    Optional<CalculationCarbon> findByEmail(String email);
    
}
