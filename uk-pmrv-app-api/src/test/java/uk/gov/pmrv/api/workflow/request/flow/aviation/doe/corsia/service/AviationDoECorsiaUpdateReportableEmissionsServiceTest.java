package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaEmissions;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;

import java.math.BigDecimal;
import java.time.Year;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaUpdateReportableEmissionsServiceTest {

    @InjectMocks
    private AviationDoECorsiaUpdateReportableEmissionsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationReportableEmissionsService reportableEmissionsService;

    @Test
    void updateReportableEmissions() {
        String requestId = "1";
        Request request = Request.builder()
                .payload(AviationDoECorsiaRequestPayload.builder()
                        .doe(AviationDoECorsia.builder()
                                .emissions(AviationDoECorsiaEmissions.builder()
                                        .emissionsClaimFromCorsiaEligibleFuels(BigDecimal.TWO)
                                        .emissionsAllInternationalFlights(BigDecimal.TWO)
                                        .emissionsFlightsWithOffsettingRequirements(BigDecimal.ONE)
                                        .calculationApproach("calc").build())
                                .fee(AviationDoECorsiaFee.builder().build())
                                .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                                        .type(
                                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                                        .furtherDetails("furtherDetails")
                                        .build())
                                .build())
                        .build())
                .metadata(AviationDoECorsiaRequestMetadata.builder()
                        .year(Year.of(2022))
                        .build())
                .build();
        final AviationReportableEmissionsSaveParams saveParams = AviationReportableEmissionsSaveParams.builder()
                .accountId(request.getAccountId())
                .year(Year.of(2022))
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(BigDecimal.TWO)
                        .reportableOffsetEmissions(BigDecimal.ONE)
                        .reportableReductionClaimEmissions(BigDecimal.TWO)
                        .build()
                )
                .isFromDre(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.updateReportableEmissions(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(reportableEmissionsService, times(1))
                .saveReportableEmissions(saveParams, EmissionTradingScheme.CORSIA);
    }
}
