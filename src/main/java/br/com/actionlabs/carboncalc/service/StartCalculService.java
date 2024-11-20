package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.enums.UF;
import br.com.actionlabs.carboncalc.exception.APIException;
import br.com.actionlabs.carboncalc.model.CalculationCarbon;
import br.com.actionlabs.carboncalc.repository.CalculationCarbonRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StartCalculService {

    @Autowired
    CalculationCarbonRepository calculationCarbonRepository;

    @Autowired
    EnergyEmissionFactorRepository energyEmissionFactorRepository;

    @Autowired
    TransportationEmissionFactorRepository transportationEmissionFactorRepository;

    @Autowired
    SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;

    public StartCalcResponseDTO registerCalc(@Valid StartCalcRequestDTO request) {
        validateUF(request.getUf());

        calculationCarbonRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    throw new APIException(
                            String.format("An account is already registered with the provided email: %s (ID: %s)",
                                    existing.getEmail(), existing.getId()),
                            HttpStatus.BAD_REQUEST
                    );
                });

        CalculationCarbon calculation = new CalculationCarbon();
        calculation.setName(request.getName());
        calculation.setEmail(request.getEmail());
        calculation.setPhoneNumber(request.getPhoneNumber());
        calculation.setUf(request.getUf());
        calculation.setCreatedAt(Instant.now());

        try {
            CalculationCarbon savedCalculation = calculationCarbonRepository.save(calculation);
            return new StartCalcResponseDTO(savedCalculation.getId());
        } catch (Exception e) {
            throw new APIException("Error saving calculation to the database. Please try again.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateUF(String uf) {
        try {
            UF.valueOf(uf);
        } catch (IllegalArgumentException e) {
            throw new APIException(
                    "Invalid UF value. Must be one of: [" + String.join(", ", EnumSet.allOf(UF.class).stream().map(Enum::name).toList()) + "]",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public UpdateCalcInfoResponseDTO updateCalculationInfo(@Valid UpdateCalcInfoRequestDTO request) {

        validateTransportationList(request.getTransportation());

        CalculationCarbon calculation = calculationCarbonRepository.findById(request.getId())
                .orElseThrow(() -> new APIException("Calculation not found with the provided ID.", HttpStatus.NOT_FOUND));

        double energyFactor = fetchEnergyFactor(calculation.getUf());
        double totalTransportationEmissions = calculateTransportationEmissions(request.getTransportation());
        double solidWasteEmissions = calculateSolidWasteEmissions(request.getSolidWasteTotal(), request.getRecyclePercentage(), calculation.getUf());

        double totalEmissions = (request.getEnergyConsumption() * energyFactor) +
                totalTransportationEmissions +
                solidWasteEmissions;

        calculation.setEnergyConsumption(request.getEnergyConsumption().doubleValue());
        calculation.setTransportation(
                request.getTransportation().stream().map(dto -> {
                    CalculationCarbon.Transportation transportation = new CalculationCarbon.Transportation();
                    transportation.setType(dto.getType().toString());
                    transportation.setDistance(dto.getMonthlyDistance());
                    return transportation;
                }).toList()
        );
        calculation.setSolidWasteProduction(request.getSolidWasteTotal().doubleValue());
        calculation.setRecyclePercentage(request.getRecyclePercentage());
        calculation.setCarbonFootprint(totalEmissions);
        calculation.setUpdatedAt(Instant.now());

        try {
            calculationCarbonRepository.save(calculation);

            UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();
            response.setSuccess(true);
            return response;

        } catch (Exception e) {
            throw new APIException("Error updating calculation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateTransportationList(List<TransportationDTO> transportationList) {
        if (transportationList == null) {
            throw new APIException("The transportation list cannot be null.", HttpStatus.BAD_REQUEST);
        }

        transportationList.forEach(dto -> {
            if (dto.getType() == null) {
                throw new APIException("The field 'type' in transportation cannot be null. Allowed types are: " +
                        EnumSet.allOf(TransportationType.class), HttpStatus.BAD_REQUEST);
            }

            if (!EnumSet.allOf(TransportationType.class).contains(dto.getType())) {
                throw new APIException("Invalid transportation type: " + dto.getType() +
                        ". Allowed types are: " + EnumSet.allOf(TransportationType.class), HttpStatus.BAD_REQUEST);
            }

            if (dto.getMonthlyDistance() == null || dto.getMonthlyDistance() <= 0) {
                throw new APIException("The field 'monthlyDistance' in transportation must be a positive value.", HttpStatus.BAD_REQUEST);
            }
        });
    }

    private double fetchEnergyFactor(String uf) {
        return energyEmissionFactorRepository.findById(uf)
                .orElseThrow(() -> new APIException("Energy emission factor not found for UF: " + uf, HttpStatus.NOT_FOUND))
                .getFactor();
    }

    private double calculateTransportationEmissions(List<TransportationDTO> transportation) {
        return transportation.stream()
                .mapToDouble(dto -> {
                    double factor = transportationEmissionFactorRepository.findById(dto.getType())
                            .orElseThrow(() -> new APIException("Transportation emission factor not found for type: " + dto.getType(), HttpStatus.NOT_FOUND))
                            .getFactor();
                    return dto.getMonthlyDistance() * factor;
                }).sum();
    }

    private double calculateSolidWasteEmissions(int solidWasteTotal, double recyclePercentage, String uf) {
        var factors = solidWasteEmissionFactorRepository.findById(uf)
                .orElseThrow(() -> new APIException("Solid waste emission factors not found for UF: " + uf, HttpStatus.NOT_FOUND));

        double recyclableWaste = solidWasteTotal * recyclePercentage;
        double nonRecyclableWaste = solidWasteTotal - recyclableWaste;

        return (recyclableWaste * factors.getRecyclableFactor()) +
                (nonRecyclableWaste * factors.getNonRecyclableFactor());
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public CarbonCalculationResultDTO getCalculationResult(String id) {
        CalculationCarbon calculation = calculationCarbonRepository.findById(id)
                .orElseThrow(() -> new APIException("Calculation not found with the provided ID.", HttpStatus.NOT_FOUND));

        double energyConsumption = calculation.getEnergyConsumption() != null ? calculation.getEnergyConsumption() : 0.0;
        double totalTransportationEmissions = 0.0;
        double solidWasteEmissions = 0.0;

        List<TransportationDTO> transportationDTOList = new ArrayList<>();
        if (calculation.getTransportation() != null && !calculation.getTransportation().isEmpty()) {
            transportationDTOList = calculation.getTransportation().stream()
                    .map(transportation -> {
                        TransportationDTO dto = new TransportationDTO();
                        dto.setType(TransportationType.valueOf(transportation.getType()));
                        dto.setMonthlyDistance(transportation.getDistance());
                        return dto;
                    }).collect(Collectors.toList());
            totalTransportationEmissions = calculateTransportationEmissions(transportationDTOList);
        }

        if (calculation.getSolidWasteProduction() != null && calculation.getRecyclePercentage() != null) {
            solidWasteEmissions = calculateSolidWasteEmissions(
                    calculation.getSolidWasteProduction().intValue(),
                    calculation.getRecyclePercentage(),
                    calculation.getUf()
            );
        }

        double energyFactor = fetchEnergyFactor(calculation.getUf());
        double totalEmissions = (energyConsumption * energyFactor) + totalTransportationEmissions + solidWasteEmissions;

        CarbonCalculationResultDTO resultDTO = new CarbonCalculationResultDTO();
        resultDTO.setEnergy(round(energyConsumption * energyFactor));
        resultDTO.setTransportation(round(totalTransportationEmissions));
        resultDTO.setSolidWaste(round(solidWasteEmissions));
        resultDTO.setTotal(round(resultDTO.getEnergy() + resultDTO.getTransportation() + resultDTO.getSolidWaste()));

        return resultDTO;
    }

}
