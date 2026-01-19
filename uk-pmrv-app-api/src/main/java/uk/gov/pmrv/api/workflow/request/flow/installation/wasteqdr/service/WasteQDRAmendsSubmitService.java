package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

@Service
@RequiredArgsConstructor
public class WasteQDRAmendsSubmitService {

    private final WasteQDRSubmitService submitService;
    private final WasteQDRValidationService validationService;

    @Transactional
    public void saveAmends(WasteQDRApplicationAmendsSaveRequestTaskActionPayload taskActionPayload,
                           RequestTask requestTask) {
        WasteQDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (WasteQDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setQdr(taskActionPayload.getQdr());
        requestTaskPayload
                .setWasteQDRSectionsCompleted(taskActionPayload.getWasteQDRSectionsCompleted());
        requestTaskPayload
                .setRegulatorReviewSectionsCompleted(taskActionPayload.getRegulatorReviewSectionsCompleted());
    }

    @Transactional
    public void submitToRegulator(WasteQDRApplicationAmendsSubmitRequestTaskActionPayload actionPayload,
                                  RequestTask requestTask, AppUser appUser) {
        WasteQDRRequestPayload requestPayload = (WasteQDRRequestPayload) requestTask.getRequest().getPayload();

        WasteQDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
                (WasteQDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateWasteQDR(requestTaskPayload.getQdr());

        requestTaskPayload.setWasteQDRSectionsCompleted(actionPayload.getWasteQDRSectionsCompleted());

        RequestActionPayload requestActionPayload = submitService.createApplicationSubmittedRequestActionPayload(
                requestTask, requestTaskPayload, RequestActionPayloadType.WASTE_QDR_APPLICATION_AMENDS_SUBMITTED_PAYLOAD);

        submitService.submitWasteQDR(requestPayload, requestTask, appUser,
                RequestActionType.WASTE_QDR_APPLICATION_AMENDS_SUBMITTED,
                requestActionPayload, requestTaskPayload.getWasteQDRSectionsCompleted());
    }
}
