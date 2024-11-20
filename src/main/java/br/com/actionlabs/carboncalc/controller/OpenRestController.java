package br.com.actionlabs.carboncalc.controller;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.service.StartCalculService;
import br.com.actionlabs.carboncalc.util.ResponseStandartDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Calculation Process", description = "Operations related to the carbon emissions calculation process.")
public class OpenRestController {

    @Autowired
    @Lazy
    StartCalculService startCalculService;

    @Operation(
            summary = "Start Calculation",
            description = "Endpoint responsible for starting a new carbon emission calculation process."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculation started successfully.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))
            ),
            @ApiResponse(responseCode = "400",description = "Invalid data provided.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))
            ),
            @ApiResponse(responseCode = "500",description = "Internal server error.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))
            )
    })
    @PostMapping("start-calc")
    public ResponseEntity<StartCalcResponseDTO> startCalculation(
            @Valid @RequestBody StartCalcRequestDTO request) {
      StartCalcResponseDTO response =  startCalculService.registerCalc(request);
      return ResponseEntity.ok().body(response);
    }


    @Operation(
            summary = "Update Calculation Info",
            description = "Endpoint responsible for updating the information required to calculate carbon emissions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculation updated successfully.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))),
            @ApiResponse(responseCode = "404", description = "Calculation not found.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class)))
    })
    @PutMapping("info")
    public ResponseEntity<UpdateCalcInfoResponseDTO> updateInfo(
            @Valid @RequestBody UpdateCalcInfoRequestDTO request) {
        UpdateCalcInfoResponseDTO response =  startCalculService.updateCalculationInfo(request);
        return ResponseEntity.ok().body(response);
  }

    @Operation(
            summary = "Get Carbon Footprint Result",
            description = "Endpoint responsible for retrieving the carbon footprint result for a given calculation ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculation result retrieved successfully.", content = @Content(schema = @Schema(implementation = CarbonCalculationResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data provided.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))),
            @ApiResponse(responseCode = "404", description = "Calculation not found.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content(schema = @Schema(implementation = ResponseStandartDTO.class)))
    })
    @GetMapping("result/{id}")
    public ResponseEntity<CarbonCalculationResultDTO> getResult(@PathVariable String id) {
        CarbonCalculationResultDTO result = startCalculService.getCalculationResult(id);
        return ResponseEntity.ok().body(result);
    }

}
