package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorReviewResponse;

@ExtendWith(MockitoExtension.class)
class AviationVirReviewedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AviationVirReviewedDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private AccountQueryService accountQueryService;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.AVIATION_VIR_REVIEWED);
    }

    @Test
    void constructParams() {
        
        final RegulatorReviewResponse regulatorReviewResponse = RegulatorReviewResponse.builder()
                .reportSummary("Report Summary")
                .build();
        final AviationVirRequestPayload payload = AviationVirRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                .regulatorReviewResponse(regulatorReviewResponse)
                .build();
        String requestId = "1";

        final long accountId = 2L;
        
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(Request.builder().accountId(accountId).build()));
        when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);
        
        assertThat(provider.constructParams(payload, requestId))
                .isEqualTo(Map.of(
                    "regulatorReviewResponse", regulatorReviewResponse,
                    "isCorsia", false
                    )
                );
    }
}
