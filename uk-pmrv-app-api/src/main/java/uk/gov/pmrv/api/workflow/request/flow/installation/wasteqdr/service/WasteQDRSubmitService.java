package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.mapper.WasteQDRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WasteQDRSubmitService {

    private final WasteQDRValidationService wasteQDRValidationService;
    private final RequestService requestService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private static final WasteQDRMapper WASTE_QDR_MAPPER = Mappers.getMapper(WasteQDRMapper.class);

    public void applySaveAction(RequestTask requestTask,
                                WasteQDRApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final WasteQDRApplicationSubmitRequestTaskPayload taskPayload =
                (WasteQDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setWasteQDRSectionsCompleted(
                taskActionPayload.getWasteQDRSectionsCompleted());
        taskPayload.setQdr(taskActionPayload.getQdr());
    }

    public void submitToRegulator(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();
        WasteQDRRequestPayload requestPayload = (WasteQDRRequestPayload) request.getPayload();
        WasteQDRApplicationSubmitRequestTaskPayload taskPayload = (WasteQDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        wasteQDRValidationService.validateWasteQDR(taskPayload.getQdr());

        RequestActionPayload actionPayload = createApplicationSubmittedRequestActionPayload(requestTask, taskPayload, RequestActionPayloadType.WASTE_QDR_APPLICATION_SUBMITTED_PAYLOAD);

        submitWasteQDR(requestPayload, requestTask, appUser, RequestActionType.WASTE_QDR_APPLICATION_SUBMITTED, actionPayload, taskPayload.getWasteQDRSectionsCompleted());

    }

    public WasteQDRApplicationSubmittedRequestActionPayload createApplicationSubmittedRequestActionPayload(RequestTask requestTask,
                                                                                                           WasteQDRApplicationSubmitRequestTaskPayload taskPayload,
                                                                                                           RequestActionPayloadType payloadType) {

        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(requestTask.getRequest().getAccountId());

        WasteQDRApplicationSubmittedRequestActionPayload actionPayload = WASTE_QDR_MAPPER.toWasteQDRApplicationSubmittedRequestActionPayload(taskPayload, payloadType);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        actionPayload.setWasteQDRAttachments(taskPayload.getWasteQDRAttachments());


        return actionPayload;
    }

    public void submitWasteQDR(WasteQDRRequestPayload wasteQDRRequestPayload,
                               RequestTask requestTask,
                               AppUser appUser,
                               RequestActionType requestActionType,
                               RequestActionPayload actionPayload,
                               Map<String, Boolean> wasteQDRSectionsCompleted) {

        final WasteQDRApplicationSubmitRequestTaskPayload taskPayload =
                (WasteQDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        wasteQDRRequestPayload.setQdr(taskPayload.getQdr());
        wasteQDRRequestPayload.setWasteQDRAttachments(taskPayload.getWasteQDRAttachments());
        wasteQDRRequestPayload.setWasteQDRSectionsCompleted(wasteQDRSectionsCompleted);

        requestService.addActionToRequest(
                requestTask.getRequest(),
                actionPayload,
                requestActionType,
                appUser.getUserId());
    }
}
