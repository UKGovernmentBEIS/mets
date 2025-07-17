package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;

@ExtendWith(MockitoExtension.class)
class RdeSubmitDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private RdeSubmitDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private AccountQueryService accountQueryService;
    
    @Test
    void getRequestTaskActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.RDE_SUBMIT);
    }
    
    @Test
    void constructParams() {
        RequestPayloadRdeable payload = PermitIssuanceRequestPayload.builder()
        		.rdeData(RdeData.builder()
        				.rdePayload(RdePayload.builder()
                                .extensionDate(LocalDate.now())
                                .deadline(LocalDate.now())
                                .build())
        				.build())
                .build();
        String requestId = "1";

        final long accountId = 2L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(Request.builder().accountId(accountId).build()));
        when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);
        
        Map<String, Object> result = provider.constructParams(payload, requestId);
        assertThat(result).containsOnlyKeys("extensionDate", "deadline", "isCorsia");
    }
    
}
