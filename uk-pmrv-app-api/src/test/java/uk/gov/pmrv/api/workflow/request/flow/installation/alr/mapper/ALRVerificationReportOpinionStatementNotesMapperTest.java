package uk.gov.pmrv.api.workflow.request.flow.installation.alr.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationReport;


import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ALRVerificationReportOpinionStatementNotesMapperTest {

    @InjectMocks
    private ALRApplicationVerificationSubmittedVerificationReportOpinionStatementNotesMapper mapper;

    @Test
    public void toRequestActionDTO() {

        UUID attachmentId = UUID.randomUUID();

        final ALRApplicationSubmittedRequestActionPayload requestActionPayload = ALRApplicationSubmittedRequestActionPayload
                .builder()
                .verificationReport(ALRVerificationReport
                        .builder()
                        .verificationData(ALRVerificationData
                                .builder()
                                .opinionStatement(ALRVerificationOpinionStatement
                                        .builder()
                                        .opinionStatementFiles(Set.of(attachmentId))
                                        .notes("Test")
                                        .build())
                                .build())
                        .build())
                .build();

        RequestAction requestAction = RequestAction.builder()
                .type(RequestActionType.ALR_APPLICATION_SENT_TO_VERIFIER)
                .payload(requestActionPayload)
                .build();


        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(requestAction.getType());
        assertThat(result.getPayload()).isInstanceOf(ALRApplicationSubmittedRequestActionPayload.class);
        assertThat(((ALRApplicationSubmittedRequestActionPayload) result.getPayload())
                .getVerificationReport()
                .getVerificationData()
                .getOpinionStatement()).isNotNull();
        assertThat(((ALRApplicationSubmittedRequestActionPayload) result.getPayload())
                .getVerificationReport()
                .getVerificationData()
                .getOpinionStatement()
                .getOpinionStatementFiles()).containsExactly(attachmentId);
        assertThat(((ALRApplicationSubmittedRequestActionPayload) result.getPayload())
                .getVerificationReport()
                .getVerificationData()
                .getOpinionStatement()
                .getNotes()).isNull();
    }
}
