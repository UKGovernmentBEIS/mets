package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;


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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRVerificationOpinionStatement;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRVerificationSubmitService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRApplicationVerificationSaveActionHandlerTest {

    @InjectMocks
    private BDRApplicationVerificationSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRVerificationSubmitService bdrVerificationSubmitService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final UUID attachmentId = UUID.randomUUID();
        final BDRApplicationVerificationSaveRequestTaskActionPayload payload = BDRApplicationVerificationSaveRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.BDR_APPLICATION_SAVE_VERIFICATION_PAYLOAD)
                .verificationData(BDRVerificationData.builder()
                        .opinionStatement(BDRVerificationOpinionStatement
                                .builder()
                                .opinionStatementFiles(Set.of(attachmentId))
                                .notes("Test")
                                .build())
                        .build())
                .build();
        final RequestTask task = RequestTask.builder().id(1L).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.BDR_SAVE_APPLICATION_VERIFICATION, user, payload);

        verify(requestTaskService, times(1))
                .findTaskById(taskId);
        verify(bdrVerificationSubmitService, times(1))
                .applySaveAction(payload, task);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.BDR_SAVE_APPLICATION_VERIFICATION), handler.getTypes());
    }
}
