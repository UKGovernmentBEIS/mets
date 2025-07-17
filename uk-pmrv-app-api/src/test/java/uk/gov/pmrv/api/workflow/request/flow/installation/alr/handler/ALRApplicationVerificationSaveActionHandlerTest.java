package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRVerificationSubmitService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRApplicationVerificationSaveActionHandlerTest {

    @InjectMocks
    private ALRApplicationVerificationSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private ALRVerificationSubmitService alrVerificationSubmitService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final UUID attachmentId = UUID.randomUUID();
        final ALRApplicationVerificationSaveRequestTaskActionPayload payload = ALRApplicationVerificationSaveRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.ALR_APPLICATION_SAVE_VERIFICATION_PAYLOAD)
                .verificationData(ALRVerificationData.builder()
                        .opinionStatement(ALRVerificationOpinionStatement
                                .builder()
                                .opinionStatementFiles(Set.of(attachmentId))
                                .notes("Test")
                                .build())
                        .build())
                .build();
        final RequestTask task = RequestTask.builder().id(1L).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.ALR_SAVE_APPLICATION_VERIFICATION, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(alrVerificationSubmitService, times(1)).applySaveAction(payload, task);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.ALR_SAVE_APPLICATION_VERIFICATION), handler.getTypes());
    }
}
