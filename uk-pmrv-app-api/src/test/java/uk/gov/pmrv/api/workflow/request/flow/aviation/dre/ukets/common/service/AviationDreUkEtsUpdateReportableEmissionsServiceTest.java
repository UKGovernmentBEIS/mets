package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Year;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproach;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreEmissionsCalculationApproachType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsUpdateReportableEmissionsServiceTest {

    @InjectMocks
    private AviationDreUkEtsUpdateReportableEmissionsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationReportableEmissionsService reportableEmissionsService;

    @Test
    void updateReportableEmissions() {
        String requestId = "1";
        Request request = Request.builder()
            .payload(AviationDreUkEtsRequestPayload.builder()
                .dre(AviationDre.builder()
                    .totalReportableEmissions(BigDecimal.valueOf(10000))
                    .fee(AviationDreFee.builder().build())
                    .calculationApproach(AviationDreEmissionsCalculationApproach.builder()
                        .type(
                            AviationDreEmissionsCalculationApproachType.VERIFIED_ANNUAL_EMISSIONS_REPORT_SUBMITTED_LATE)
                        .otherDataSourceExplanation("otherSources")
                        .build())
                    .determinationReason(AviationDreDeterminationReason.builder()
                        .type(
                            AviationDreDeterminationReasonType.IMPOSING_OR_CONSIDERING_IMPOSING_CIVIL_PENALTY_IN_ACCORDANCE_WITH_ORDER)
                        .furtherDetails("furtherDetails")
                        .build())
                    .build())
                .build())
            .metadata(AviationDreRequestMetadata.builder()
                .year(Year.of(2022))
                .build())
            .build();
        final AviationReportableEmissionsSaveParams saveParams = AviationReportableEmissionsSaveParams.builder()
                .accountId(request.getAccountId())
                .year(Year.of(2022))
                .reportableEmissions(AviationAerUkEtsTotalReportableEmissions.builder()
                        .reportableEmissions(BigDecimal.valueOf(10000))
                        .build()
                )
                .isFromDre(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.updateReportableEmissions(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(reportableEmissionsService, times(1))
                .saveReportableEmissions(saveParams, EmissionTradingScheme.UK_ETS_AVIATION);
    }
}
