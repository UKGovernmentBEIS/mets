package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2VerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2VerificationSubmitService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BDRS2ApplicationVerificationSaveActionHandlerTest {

    @InjectMocks
    private BDRS2ApplicationVerificationSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRS2VerificationSubmitService bdrs2VerificationSubmitService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final UUID attachmentId = UUID.randomUUID();

        final BDRS2ApplicationVerificationSaveRequestTaskActionPayload payload =
                BDRS2ApplicationVerificationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.BDRS2_APPLICATION_SAVE_VERIFICATION_PAYLOAD)
                        .verificationData(BDRS2VerificationData.builder()
                                .opinionStatement(BDRS2VerificationOpinionStatement.builder()
                                        .opinionStatementFile(attachmentId)
                                        .supportingFiles(Set.of(attachmentId))
                                        .notes("Test")
                                        .build())
                                .build())
                        .build();

        final RequestTask task = RequestTask.builder().id(taskId).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(
                taskId,
                RequestTaskActionType.BDRS2_SAVE_APPLICATION_VERIFICATION,
                user,
                payload
        );

        verify(requestTaskService, times(1))
                .findTaskById(taskId);
        verify(bdrs2VerificationSubmitService, times(1))
                .applySaveAction(payload, task);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(
                List.of(RequestTaskActionType.BDRS2_SAVE_APPLICATION_VERIFICATION),
                handler.getTypes()
        );
    }
}
