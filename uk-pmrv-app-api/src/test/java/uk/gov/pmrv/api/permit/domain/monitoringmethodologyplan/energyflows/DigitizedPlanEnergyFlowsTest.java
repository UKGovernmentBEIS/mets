package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class DigitizedPlanEnergyFlowsTest {

    private static Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    public void valid_FuelInputFlows() throws Exception {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        FuelInputFlows fuelInputFlows = energyFlows.getFuelInputFlows();
        assertTrue(validator.validate(fuelInputFlows).isEmpty());
    }


    @Test
    public void invalid_FuelInputFlows() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        FuelInputFlows fuelInputFlows = energyFlows.getFuelInputFlows();
        fuelInputFlows.setHierarchicalOrder(null);
        assertFalse(validator.validate(fuelInputFlows).isEmpty());
    }

    @Test
    public void validate_fuelInputFlows_json_mapping() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        FuelInputFlows fuelInputFlows = energyFlows.getFuelInputFlows();
        assertEquals("description",fuelInputFlows.getMethodologyAppliedDescription());
        assertTrue(fuelInputFlows.getHierarchicalOrder().isFollowed());
        assertEquals(1,fuelInputFlows.getFuelInputDataSources().size());
        assertEquals("1",fuelInputFlows.getFuelInputDataSources().getFirst().getDataSourceNumber());
        assertEquals(AnnexVIISection44.METHOD_MONITORING_PLAN,fuelInputFlows.getFuelInputDataSources().getFirst().getFuelInput());
        assertEquals(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN,fuelInputFlows.getFuelInputDataSources().getFirst().getEnergyContent());

    }

    @Test
    public void valid_measurableHeatFlows() throws Exception {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        MeasurableHeatFlows measurableHeatFlows = energyFlows.getMeasurableHeatFlows();
        assertTrue(validator.validate(measurableHeatFlows).isEmpty());
    }


    @Test
    public void invalid_measurableHeatFlows() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        MeasurableHeatFlows measurableHeatFlows = energyFlows.getMeasurableHeatFlows();
        measurableHeatFlows.setMethodologyAppliedDescription(null);
        assertFalse(validator.validate(measurableHeatFlows).isEmpty());
    }

    @Test
    public void validate_measurableHeatFlows_json_mapping() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        MeasurableHeatFlows measurableHeatFlows = energyFlows.getMeasurableHeatFlows();
        assertEquals("description",measurableHeatFlows.getMethodologyAppliedDescription());
        assertTrue(measurableHeatFlows.getHierarchicalOrder().isFollowed());
        assertEquals(1,measurableHeatFlows.getMeasurableHeatFlowsDataSources().size());
        assertEquals("1",measurableHeatFlows.getMeasurableHeatFlowsDataSources().getFirst().getDataSourceNumber());
        assertEquals(AnnexVIISection45.LEGAL_METROLOGICAL_CONTROL_READING,measurableHeatFlows.getMeasurableHeatFlowsDataSources().getFirst().getQuantification());
        assertEquals(AnnexVIISection72.MEASUREMENTS,measurableHeatFlows.getMeasurableHeatFlowsDataSources().getFirst().getNet());

    }

    @Test
    public void valid_wasteGasFlows() throws Exception {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        WasteGasFlows wasteGasFlows = energyFlows.getWasteGasFlows();
        assertTrue(validator.validate(wasteGasFlows).isEmpty());
    }


    @Test
    public void invalid_wasteGasFlows() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        WasteGasFlows wasteGasFlows = energyFlows.getWasteGasFlows();
        wasteGasFlows.setMethodologyAppliedDescription(null);
        assertFalse(validator.validate(wasteGasFlows).isEmpty());
    }

    @Test
    public void validate_wasteGasFlows_json_mapping() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        WasteGasFlows wasteGasFlows = energyFlows.getWasteGasFlows();
        assertEquals("description",wasteGasFlows.getMethodologyAppliedDescription());
        assertTrue(wasteGasFlows.getHierarchicalOrder().isFollowed());
        assertEquals(1,wasteGasFlows.getWasteGasFlowsDataSources().size());
        assertEquals("1",wasteGasFlows.getWasteGasFlowsDataSources().getFirst().getDataSourceNumber());
        assertEquals(AnnexVIISection44.INDIRECT_DETERMINATION,wasteGasFlows.getWasteGasFlowsDataSources().getFirst().getQuantification());
        assertEquals(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61,wasteGasFlows.getWasteGasFlowsDataSources().getFirst().getEnergyContent());
    }

    @Test
    public void valid_electricityFlows() throws Exception {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        ElectricityFlows electricityFlows = energyFlows.getElectricityFlows();
        ElectricityFlows electricityFlows1 = ElectricityFlows.builder().electricityProduced(false).build();
        assertTrue(validator.validate(electricityFlows).isEmpty());
        assertTrue(validator.validate(electricityFlows1).isEmpty());

    }


    @Test
    public void invalid_electricityFlows() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        ElectricityFlows electricityFlows = energyFlows.getElectricityFlows();
        electricityFlows.setMethodologyAppliedDescription(null);
        assertFalse(validator.validate(electricityFlows).isEmpty());
    }

    @Test
    public void validate_electricityFlows_json_mapping() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        ElectricityFlows electricityFlows = energyFlows.getElectricityFlows();
        assertEquals("description",electricityFlows.getMethodologyAppliedDescription());
        assertTrue(electricityFlows.getHierarchicalOrder().isFollowed());
        assertEquals(1,electricityFlows.getElectricityFlowsDataSources().size());
        assertEquals("1",electricityFlows.getElectricityFlowsDataSources().getFirst().getDataSourceNumber());
        assertEquals(AnnexVIISection45.OTHER_METHODS,electricityFlows.getElectricityFlowsDataSources().getFirst().getQuantification());
    }

    @Test
    public void valid_energyFlows() throws IOException {
        EnergyFlows energyFlows = getEnergyFlowsFromFile();
        assertTrue(validator.validate(energyFlows).isEmpty());
    }


    private EnergyFlows getEnergyFlowsFromFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent =
                new String(Files.readAllBytes(Paths.get(String.format("src/test/resources/files/mmp/%s.json", "digitizedplan_energyFlows"))));
        return objectMapper.readValue(jsonContent, EnergyFlows.class);
    }

}
