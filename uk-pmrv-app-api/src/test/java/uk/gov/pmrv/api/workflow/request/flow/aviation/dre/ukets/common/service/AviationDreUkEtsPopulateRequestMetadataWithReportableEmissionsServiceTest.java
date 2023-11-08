package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class AviationDreUkEtsPopulateRequestMetadataWithReportableEmissionsServiceTest {

    @InjectMocks
    private AviationDreUkEtsPopulateRequestMetadataWithReportableEmissionsService service;

    @Mock
    private RequestService requestService;

    @Test
    void updateRequestMetadata() {
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
            .metadata(AviationDreRequestMetadata.builder().build())
            .build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.updateRequestMetadata(requestId);

        verify(requestService, times(1)).findRequestById(requestId);

        assertThat(((AviationDreRequestMetadata) request.getMetadata()).getEmissions())
            .isEqualTo(((AviationDreUkEtsRequestPayload) request.getPayload()).getDre().getTotalReportableEmissions());
    }
}
