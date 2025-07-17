package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.ImportedExportedAmountsDataSource;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.DirectlyAttributableEmissionsPBDataSourceValidator;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DirectlyAttributableEmissionsPBDataSourceValidatorTest {

    @InjectMocks
    private DirectlyAttributableEmissionsPBDataSourceValidator validator;

    @Test
    void validateDataSources_emptyDataSources_shouldReturnTrue() {
        DirectlyAttributableEmissionsPB data = DirectlyAttributableEmissionsPB.builder()
                .dataSources(Collections.emptyList())
                .furtherInternalSourceStreamsRelevant(false)
                .transferredCO2ImportedOrExportedRelevant(false)
                .build();

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_validFirstDataSource_shouldReturnTrue() {
        ImportedExportedAmountsDataSource dataSource = new ImportedExportedAmountsDataSource();
        dataSource.setImportedExportedAmountsDataSourceNo("1");
        dataSource.setAmounts(AnnexVIISection44.INDIRECT_DETERMINATION);

        DirectlyAttributableEmissionsPB data = DirectlyAttributableEmissionsPB.builder()
                .dataSources(List.of(dataSource))
                .furtherInternalSourceStreamsRelevant(true)
                .transferredCO2ImportedOrExportedRelevant(true)
                .build();

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_invalidFirstDataSource_shouldReturnFalse() {
        ImportedExportedAmountsDataSource dataSource = new ImportedExportedAmountsDataSource();
        dataSource.setImportedExportedAmountsDataSourceNo("1");
        // amounts is null

        DirectlyAttributableEmissionsPB data = DirectlyAttributableEmissionsPB.builder()
                .dataSources(List.of(dataSource))
                .furtherInternalSourceStreamsRelevant(true)
                .transferredCO2ImportedOrExportedRelevant(true)
                .build();

        assertFalse(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_validAdditionalDataSource_shouldReturnTrue() {
        ImportedExportedAmountsDataSource first = new ImportedExportedAmountsDataSource();
        first.setImportedExportedAmountsDataSourceNo("1");
        first.setAmounts(AnnexVIISection44.METHOD_MONITORING_PLAN);

        ImportedExportedAmountsDataSource second = new ImportedExportedAmountsDataSource();
        second.setImportedExportedAmountsDataSourceNo("2");
        second.setEnergyContent(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61);

        DirectlyAttributableEmissionsPB data = DirectlyAttributableEmissionsPB.builder()
                .dataSources(List.of(first, second))
                .furtherInternalSourceStreamsRelevant(true)
                .transferredCO2ImportedOrExportedRelevant(true)
                .build();

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_invalidAdditionalDataSource_shouldReturnFalse() {
        ImportedExportedAmountsDataSource first = new ImportedExportedAmountsDataSource();
        first.setImportedExportedAmountsDataSourceNo("1");
        first.setAmounts(AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL);

        ImportedExportedAmountsDataSource second = new ImportedExportedAmountsDataSource();
        second.setImportedExportedAmountsDataSourceNo("2");
        // all fields null

        DirectlyAttributableEmissionsPB data = DirectlyAttributableEmissionsPB.builder()
                .dataSources(List.of(first, second))
                .furtherInternalSourceStreamsRelevant(false)
                .transferredCO2ImportedOrExportedRelevant(false)
                .build();

        assertFalse(validator.validateDataSources(data));
    }


}
