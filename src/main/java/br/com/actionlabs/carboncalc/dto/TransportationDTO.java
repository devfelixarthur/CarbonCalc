package br.com.actionlabs.carboncalc.dto;

import br.com.actionlabs.carboncalc.enums.TransportationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportationDTO {

  @NotNull(message = "The field 'type' cannot be null.")
  private TransportationType type;

  @NotNull(message = "The field 'monthlyDistance' cannot be null.")
  @Positive(message = "The field 'monthlyDistance' must be a positive value.")
  private Integer monthlyDistance;

}
