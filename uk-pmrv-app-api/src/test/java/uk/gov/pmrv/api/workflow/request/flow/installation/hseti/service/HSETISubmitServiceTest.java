package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETIValidatorService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETISubmitServiceTest {

    @InjectMocks
    private HSETISubmitService submitService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private HSETIValidatorService hsetiValidatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void applySaveAction() {

        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        final UUID hsetiFile = UUID.randomUUID();
        expectedSectionsCompleted.put("test",false);

        final HSETIApplicationSubmitRequestTaskPayload expectedTaskPayload =
                HSETIApplicationSubmitRequestTaskPayload
                        .builder()
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final HSETIApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                HSETIApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.HSE_TI_APPLICATION_SAVE_PAYLOAD)
                        .hseti(HSETI.builder().hsetiFile(hsetiFile).notes("test").build())
                        .hsetiSectionsCompleted(expectedSectionsCompleted)
                        .build();

        submitService.save(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getHseti(), expectedTaskActionPayload.getHseti());
        assertEquals(expectedTaskPayload.getHsetiSectionsCompleted(), expectedTaskActionPayload.getHsetiSectionsCompleted());
    }

    @Test
    void cancel() {
        final String requestId = "1";
        final String operatorAssignee = "op";

        final Request request = Request.builder()
                .id(requestId)
                .payload(HSETIRequestPayload.builder()
                        .operatorAssignee(operatorAssignee)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        submitService.cancel(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, RequestActionType.HSE_TI_APPLICATION_CANCELLED, operatorAssignee);
    }


    @Test
    void uploadAttachment() {
        final Long requestTaskId = 1L;
        final String fileName = "name";
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(HSETIApplicationSubmitRequestTaskPayload.builder().build())
                .build();
        final String attachmentUuid = UUID.randomUUID().toString();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        submitService.uploadAttachment(requestTaskId, attachmentUuid, fileName);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        assertThat(requestTask.getPayload().getAttachments()).containsEntry(UUID.fromString(attachmentUuid), fileName);
    }



    @Test
    void submitToRegulator() {
        Long requestTaskId = 1L;

        UUID hsetiFile = UUID.randomUUID();
        HSETI hseti = HSETI.builder().hsetiFile(hsetiFile).files(Set.of(hsetiFile)).notes("test").build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(HSETIApplicationSubmitRequestTaskPayload.builder().hseti(hseti).build())
                .request(Request.builder()
                        .payload(HSETIRequestPayload
                                .builder()
                                .hseti(hseti)
                                .build())
                        .build())
                .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        submitService.submitToRegulator(requestTask, appUser);

        verify(hsetiValidatorService, times(1)).validateHSETI(hseti);
    }


}
