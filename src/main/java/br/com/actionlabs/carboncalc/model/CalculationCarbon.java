package br.com.actionlabs.carboncalc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document("calculationsCarbons")
@TypeAlias("calculationCarbons")
@JsonIgnoreProperties(value = "_class", allowGetters = true)
public class CalculationCarbon {

    @Id
    private String id;

    private String name;

    private String email;
    private String phoneNumber;
    private String uf;

    private Double energyConsumption;
    private List<Transportation> transportation;
    private Double solidWasteProduction;
    private Double recyclePercentage;
    private Double carbonFootprint;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    public static class Transportation {
        private String type;
        private Integer distance;
    }

}
