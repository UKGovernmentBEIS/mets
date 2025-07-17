package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationReport;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRVerificationReportOpinionStatementNotesMapperTest {

    @InjectMocks
    private BDRApplicationVerificationSubmittedVerificationReportOpinionStatementNotesMapper mapper;

    @Test
    public void toRequestActionDTO() {

        UUID attachmentId = UUID.randomUUID();

        final BDRApplicationSubmittedRequestActionPayload requestActionPayload = BDRApplicationSubmittedRequestActionPayload
                .builder()
                .verificationReport(BDRVerificationReport
                        .builder()
                        .verificationData(BDRVerificationData
                                .builder()
                                .opinionStatement(BDRVerificationOpinionStatement
                                        .builder()
                                        .opinionStatementFiles(Set.of(attachmentId))
                                        .notes("Test")
                                        .build())
                          .build())
                        .build())
                .build();

        RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.BDR_APPLICATION_SENT_TO_VERIFIER)
    			.payload(requestActionPayload)
    			.build();


        RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(BDRApplicationSubmittedRequestActionPayload.class);
    	assertThat(((BDRApplicationSubmittedRequestActionPayload) result.getPayload())
                .getVerificationReport()
                .getVerificationData()
                .getOpinionStatement()).isNotNull();
        assertThat(((BDRApplicationSubmittedRequestActionPayload) result.getPayload())
                .getVerificationReport()
                .getVerificationData()
                .getOpinionStatement()
                .getOpinionStatementFiles()).containsExactly(attachmentId);
        assertThat(((BDRApplicationSubmittedRequestActionPayload) result.getPayload())
                .getVerificationReport()
                .getVerificationData()
                .getOpinionStatement()
                .getNotes()).isNull();
    }

}
