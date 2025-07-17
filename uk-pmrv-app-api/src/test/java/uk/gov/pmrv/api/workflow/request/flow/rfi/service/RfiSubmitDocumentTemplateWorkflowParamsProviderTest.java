package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
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
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiQuestionPayload;

@ExtendWith(MockitoExtension.class)
class RfiSubmitDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private RfiSubmitDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private AccountQueryService accountQueryService;
    
    @Test
    void getRequestTaskActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.RFI_SUBMIT);
    }
    
    @Test
    void constructParams() {
        RequestPayloadRfiable payload = PermitIssuanceRequestPayload.builder()
        		.rfiData(RfiData.builder()
        				.rfiDeadline(LocalDate.now().plusDays(10))
                        .rfiQuestionPayload(RfiQuestionPayload.builder()
                                .questions(List.of("quest1", "quest2"))
                                .build())
                		.build())
                .build();
        String requestId = "1";

        final long accountId = 2L;

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(Request.builder().accountId(accountId).build()));
        when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);
        
        Map<String, Object> result = provider.constructParams(payload, requestId);
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "deadline", Date.from(payload.getRfiData().getRfiDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "questions", payload.getRfiData().getRfiQuestionPayload().getQuestions(),
                "isCorsia", false
                ));
    }
}
