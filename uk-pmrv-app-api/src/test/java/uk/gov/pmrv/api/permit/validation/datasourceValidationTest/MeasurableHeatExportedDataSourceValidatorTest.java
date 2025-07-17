package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExportedDataSource;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.MeasurableHeatExportedDataSourceValidator;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MeasurableHeatExportedDataSourceValidatorTest {

    private final MeasurableHeatExportedDataSourceValidator validator = new MeasurableHeatExportedDataSourceValidator();

    @Test
    void validateDataSources_notMeasurableHeatExported_shouldReturnTrue() {
        MeasurableHeatExported data = new MeasurableHeatExported();
        data.setMeasurableHeatExported(false); // skip validation

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_firstDataSourceMissingNetMeasurableHeatFlows_shouldReturnFalse() {
        MeasurableHeatExportedDataSource first = new MeasurableHeatExportedDataSource();
        first.setDataSourceNo("001");
        first.setNetMeasurableHeatFlows(null); // invalid

        MeasurableHeatExported data = new MeasurableHeatExported();
        data.setMeasurableHeatExported(true);
        data.setDataSources(List.of(first));

        assertFalse(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_additionalDataSourceMissingBothFields_shouldReturnFalse() {
        MeasurableHeatExportedDataSource first = new MeasurableHeatExportedDataSource();
        first.setDataSourceNo("001");
        first.setNetMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION); // valid first

        MeasurableHeatExportedDataSource second = new MeasurableHeatExportedDataSource();
        second.setDataSourceNo("002");
        second.setNetMeasurableHeatFlows(null);
        second.setHeatExported(null); // invalid additional

        MeasurableHeatExported data = new MeasurableHeatExported();
        data.setMeasurableHeatExported(true);
        data.setDataSources(List.of(first, second));

        assertFalse(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_allValid_shouldReturnTrue() {
        MeasurableHeatExportedDataSource first = new MeasurableHeatExportedDataSource();
        first.setDataSourceNo("001");
        first.setNetMeasurableHeatFlows(AnnexVIISection72.PROXY_MEASURED_EFFICIENCY);

        MeasurableHeatExportedDataSource second = new MeasurableHeatExportedDataSource();
        second.setDataSourceNo("002");
        second.setNetMeasurableHeatFlows(AnnexVIISection72.MEASUREMENTS);
        second.setHeatExported(AnnexVIISection45.OTHER_METHODS);

        MeasurableHeatExported data = new MeasurableHeatExported();
        data.setMeasurableHeatExported(true);
        data.setDataSources(List.of(first, second));

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_onlyOneValidDataSource_shouldReturnTrue() {
        MeasurableHeatExportedDataSource first = new MeasurableHeatExportedDataSource();
        first.setDataSourceNo("001");
        first.setNetMeasurableHeatFlows(AnnexVIISection72.PROXY_REFERENCE_EFFICIENCY);

        MeasurableHeatExported data = new MeasurableHeatExported();
        data.setMeasurableHeatExported(true);
        data.setDataSources(List.of(first));

        assertTrue(validator.validateDataSources(data));
    }

    @Test
    void validateDataSources_firstDataSourceWithNullDataSourceNo_shouldStillValidateOrdering() {
        MeasurableHeatExportedDataSource first = new MeasurableHeatExportedDataSource();
        first.setDataSourceNo(null);
        first.setNetMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION);

        MeasurableHeatExported data = new MeasurableHeatExported();
        data.setMeasurableHeatExported(true);
        data.setDataSources(List.of(first));

        assertTrue(validator.validateDataSources(data));
    }
}
