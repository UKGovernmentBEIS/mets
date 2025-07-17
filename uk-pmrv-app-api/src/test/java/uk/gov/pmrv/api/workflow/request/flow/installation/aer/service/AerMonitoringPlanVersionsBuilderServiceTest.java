package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service.PermitVariationRequestQueryService;

@ExtendWith(MockitoExtension.class)
class AerMonitoringPlanVersionsBuilderServiceTest {

    @Mock
    private AerRequestQueryService aerRequestQueryService;

    @Mock
    private PermitVariationRequestQueryService permitVariationRequestQueryService;

    @InjectMocks
    private AerMonitoringPlanVersionsBuilderService aerMonitoringPlanVersionsBuilderService;

    @Test
    void buildMonitoringPlanVersions() {
        final long accountId = 1L;
        final String permitId = "permit";
        final Year reportingYear = Year.now().minusYears(1);
        final LocalDate permitVariationEndDate = LocalDate.now().minusYears(1);
        final LocalDate permitIssuanceEndDate = LocalDate.now().minusYears(2);

        final MonitoringPlanVersion monitoringPlanVersionIssuance = MonitoringPlanVersion.builder()
            .permitId(permitId)
            .permitConsolidationNumber(1)
            .endDate(permitIssuanceEndDate).build();
        final MonitoringPlanVersion monitoringPlanVersionVariation = MonitoringPlanVersion.builder()
            .permitId(permitId)
            .permitConsolidationNumber(10)
            .endDate(permitVariationEndDate).build();
        final PermitVariationRequestInfo permitVariationRequestInfo = PermitVariationRequestInfo.builder()
            .id("permitvariation")
            .endDate(permitVariationEndDate.atStartOfDay())
            .metadata(PermitVariationRequestMetadata.builder()
                .logChanges("some log changes")
                .permitConsolidationNumber(10)
                .type(RequestMetadataType.PERMIT_VARIATION)
                .rfiResponseDates(Collections.emptyList())
                .build()
            )
            .build();

        final List<MonitoringPlanVersion> expected = List.of(monitoringPlanVersionVariation, monitoringPlanVersionIssuance);

        when(aerRequestQueryService.findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(accountId))
            .thenReturn(Optional.of(permitIssuanceEndDate.atStartOfDay()));
        when(permitVariationRequestQueryService.findApprovedPermitVariationRequests(accountId))
            .thenReturn(List.of(permitVariationRequestInfo));

        // Invoke
        List<MonitoringPlanVersion> actual =
            aerMonitoringPlanVersionsBuilderService.buildMonitoringPlanVersions(accountId, permitId, reportingYear);


        // Verify
        verify(permitVariationRequestQueryService, times(1)).findApprovedPermitVariationRequests(accountId);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

    }
}