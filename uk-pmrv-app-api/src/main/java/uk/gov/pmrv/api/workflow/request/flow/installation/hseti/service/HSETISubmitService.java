package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.mapper.HSETIMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETIValidatorService;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HSETISubmitService {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final HSETIValidatorService hsetiValidatorService;
    private static final HSETIMapper HSE_TI_MAPPER = Mappers.getMapper(HSETIMapper.class);

    public void save(RequestTask requestTask,
                                HSETIApplicationSaveRequestTaskActionPayload taskActionPayload) {

        final HSETIApplicationSubmitRequestTaskPayload taskPayload =
                (HSETIApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setHsetiSectionsCompleted(
                taskActionPayload.getHsetiSectionsCompleted());
        taskPayload.setHseti(taskActionPayload.getHseti());
    }


    public void cancel(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request, null,
                RequestActionType.HSE_TI_APPLICATION_CANCELLED,
                request.getPayload().getOperatorAssignee());
    }


    @Transactional
    public void uploadAttachment(final Long requestTaskId,
                                 final String attachmentUuid,
                                 final String filename) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final HSETIApplicationSubmitRequestTaskPayload requestTaskPayload =
                (HSETIApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.getHsetiAttachments().put(UUID.fromString(attachmentUuid), filename);
    }

    public void submitToRegulator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        HSETIRequestPayload requestPayload = (HSETIRequestPayload) request.getPayload();
        HSETIApplicationSubmitRequestTaskPayload taskPayload = (HSETIApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        hsetiValidatorService.validateHSETI(taskPayload.getHseti());

        RequestActionPayload actionPayload = createApplicationSubmittedRequestActionPayload(taskPayload,
                RequestActionPayloadType.HSE_TI_APPLICATION_SUBMITTED_PAYLOAD);

        submitHSETI(requestPayload, requestTask, appUser, RequestActionType.HSE_TI_APPLICATION_SENT_TO_REGULATOR,
                actionPayload, taskPayload.getHsetiSectionsCompleted());
    }

    private HSETIApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(HSETIApplicationSubmitRequestTaskPayload taskPayload,
                                                                                                         RequestActionPayloadType payloadType) {

        HSETIApplicationSubmittedRequestActionPayload actionPayload = HSE_TI_MAPPER.toHSETIApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setHsetiAttachments(taskPayload.getHsetiAttachments());

        return actionPayload;
    }

    private void submitHSETI(HSETIRequestPayload hsetiRequestPayload,
                          RequestTask requestTask,
                          AppUser appUser,
                          RequestActionType requestActionType,
                          RequestActionPayload actionPayload,
                          Map<String, Boolean> hsetiSectionsCompleted) {

        final HSETIApplicationSubmitRequestTaskPayload taskPayload =
                (HSETIApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        hsetiRequestPayload.setHseti(taskPayload.getHseti());
        hsetiRequestPayload.setHsetiAttachments(taskPayload.getHsetiAttachments());
        hsetiRequestPayload.setHsetiSectionsCompleted(hsetiSectionsCompleted);

        requestService.addActionToRequest(
                requestTask.getRequest(),
                actionPayload,
                requestActionType,
                appUser.getUserId());
    }
}
