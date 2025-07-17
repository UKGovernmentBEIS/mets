package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedDataSourceDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedType;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.MeasurableHeatImportedDataSourceValidator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MeasurableHeatImportedDataSourceValidatorTest {

    @InjectMocks
    private MeasurableHeatImportedDataSourceValidator validator;


    @Test
    void validateDataSources_validFirstDataSource() {
        MeasurableHeatImported measurableHeatImported = MeasurableHeatImported.builder()
                        .measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP))
                        .dataSources(List.of(MeasurableHeatImportedDataSource.builder()
                            .dataSourceNo("0")
                            .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                .netContent(AnnexVIISection72.DOCUMENTATION).build())).build()))
                            .build();

        assertTrue(validator.validateDataSources(measurableHeatImported));
    }

    @Test
    void validateDataSources_invalidFirstDataSource() {
        MeasurableHeatImported measurableHeatImported = MeasurableHeatImported.builder()
                .measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP))
                .dataSources(List.of(MeasurableHeatImportedDataSource.builder()
                        .dataSourceNo("0")
                        .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                .entity(null)
                                .netContent(AnnexVIISection72.DOCUMENTATION).build())).build()))
                .build();

        assertFalse(validator.validateDataSources(measurableHeatImported));
    }

    @Test
    void validateDataSources_validAdditionalDataSource() {
        MeasurableHeatImported measurableHeatImported = MeasurableHeatImported.builder()
                .measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP))
                .dataSources(List.of(MeasurableHeatImportedDataSource.builder()
                        .dataSourceNo("0")
                        .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                .netContent(AnnexVIISection72.DOCUMENTATION).build())).build(),
                        MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(null).build())).build()
                        ))
                .build();

        assertTrue(validator.validateDataSources(measurableHeatImported));
    }

    @Test
    void validateDataSources_invalidAdditionalDataSource() {
        MeasurableHeatImported measurableHeatImported = MeasurableHeatImported.builder()
                .measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP))
                .dataSources(List.of(MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(AnnexVIISection72.DOCUMENTATION).build())).build(),
                        MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(null)
                                        .netContent(null).build())).build()
                ))
                .build();

        assertFalse(validator.validateDataSources(measurableHeatImported));
    }

    @Test
    void validateDataSources_validActivities() {
        MeasurableHeatImported measurableHeatImported = MeasurableHeatImported.builder()
                .measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP))
                .dataSources(List.of(MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(AnnexVIISection72.DOCUMENTATION).build())).build(),
                        MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(null).build())).build()
                ))
                .build();

        assertTrue(validator.validateDataSources(measurableHeatImported));

        measurableHeatImported.setMeasurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED));
        measurableHeatImported.setDataSources(null);

        assertTrue(validator.validateDataSources(measurableHeatImported));
    }

    @Test
    void validateDataSources_invalidActivities() {
        MeasurableHeatImported measurableHeatImported = MeasurableHeatImported.builder()
                .measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP))
                .dataSources(List.of(MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PRODUCT_BENCHMARK,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(AnnexVIISection72.DOCUMENTATION).build())).build(),
                        MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_WASTE_GAS,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(null).build())).build()
                ))
                .build();

        assertFalse(validator.validateDataSources(measurableHeatImported));
    }


    @Test
    void validateDataSources_validAdditionalDataSourceMultipleCategories() {
        MeasurableHeatImported measurableHeatImported = MeasurableHeatImported.builder()
                .measurableHeatImportedActivities(Set.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_WASTE_GAS))
                .dataSources(List.of(MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("0")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(AnnexVIISection72.DOCUMENTATION).build(),MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_WASTE_GAS,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(AnnexVIISection72.DOCUMENTATION).build())).build(),
                        MeasurableHeatImportedDataSource.builder()
                                .dataSourceNo("1")
                                .measurableHeatImportedActivityDetails(Map.of(MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_PULP,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(AnnexVIISection45.NOT_OPERATOR_CONTROL_DIRECT_READING_NOT_A)
                                        .netContent(null).build(),MeasurableHeatImportedType.MEASURABLE_HEAT_IMPORTED_WASTE_GAS,MeasurableHeatImportedDataSourceDetails.builder()
                                        .entity(null)
                                        .netContent(null).build())).build()
                ))
                .build();

        assertTrue(validator.validateDataSources(measurableHeatImported));
    }
}
