package br.com.actionlabs.carboncalc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StartCalcRequestDTO {

  @NotNull(message = "The field 'name' cannot be null.")
  @Size(min = 3, message = "The field 'name' must have at least 3 characters.")
  private String name;

  @NotNull(message = "The field 'email' cannot be null.")
  @Email(message = "The field 'email' must be a valid email address.")
  private String email;

  @NotNull(message = "The field 'uf' cannot be null.")
  private String uf;

  @NotNull(message = "The field 'phoneNumber' cannot be null.")
  @Size(min = 3, message = "The field 'phoneNumber' must have at least 3 characters.")
  private String phoneNumber;

}
