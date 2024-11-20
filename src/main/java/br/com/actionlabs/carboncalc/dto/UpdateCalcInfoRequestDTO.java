package br.com.actionlabs.carboncalc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCalcInfoRequestDTO {
  @NotNull(message = "The field 'id' cannot be null.")
  private String id;

  @NotNull(message = "The field 'energyConsumption' cannot be null.")
  @Positive(message = "The field 'energyConsumption' must be a positive value.")
  private Integer energyConsumption;

  @NotNull(message = "The field 'transportation' cannot be null.")
  private List<TransportationDTO> transportation;

  @NotNull(message = "The field 'solidWasteTotal' cannot be null.")
  @Positive(message = "The field 'solidWasteTotal' must be a positive value.")
  private Integer solidWasteTotal;

  @NotNull(message = "The field 'recyclePercentage' cannot be null.")
  @Min(value = 0, message = "The field 'recyclePercentage' must be at least 0.")
  @Max(value = 1, message = "The field 'recyclePercentage' must not exceed 1.0.")
  private Double recyclePercentage;

}
