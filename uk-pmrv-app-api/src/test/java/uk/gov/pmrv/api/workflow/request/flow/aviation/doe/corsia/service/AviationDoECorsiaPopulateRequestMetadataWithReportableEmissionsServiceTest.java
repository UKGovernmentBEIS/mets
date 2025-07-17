package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.*;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaPopulateRequestMetadataWithReportableEmissionsServiceTest {

    @InjectMocks
    private AviationDoECorsiaPopulateRequestMetadataWithReportableEmissionsService service;

    @Mock
    private RequestService requestService;

    @Test
    void updateRequestMetadata() {
        String requestId = "1";
        Request request = Request.builder()
                .payload(AviationDoECorsiaRequestPayload.builder()
                        .doe(AviationDoECorsia.builder()
                                .emissions(AviationDoECorsiaEmissions.builder().emissionsClaimFromCorsiaEligibleFuels(BigDecimal.TEN).build())
                                .fee(AviationDoECorsiaFee.builder().build())
                                .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                                        .type(
                                                AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                                        .furtherDetails("furtherDetails")
                                        .build())
                                .build())
                        .build())
                .metadata(AviationDoECorsiaRequestMetadata.builder().build())
                .build();
        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.updateRequestMetadata(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
    }
}
