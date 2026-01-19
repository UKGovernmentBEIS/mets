package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.mapper.WasteQDRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

@Service
@RequiredArgsConstructor
public class WasteQDRReviewService {

    private final WasteQDRValidationService validationService;
    private final RequestService requestService;


    private static final WasteQDRMapper WASTE_QDR_MAPPER = Mappers.getMapper(WasteQDRMapper.class);


    @Transactional
    public void saveReviewDecision(WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
                                   RequestTask requestTask) {
        final WasteQDRReviewDecision reviewDecision = taskActionPayload.getReviewDecision();

        final WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setReviewDecision(reviewDecision);
        taskPayload.setRegulatorReviewSectionsCompleted(taskActionPayload.getRegulatorReviewSectionsCompleted());
    }

    @Transactional
    public void returnForAmends(RequestTask requestTask, AppUser appUser) {
        Request request = requestTask.getRequest();

        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        validationService.validateReturnForAmends(reviewRequestTaskPayload);

        updateRequestPayload(requestTask, appUser);

        WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload requestActionPayload = WASTE_QDR_MAPPER
                .toWasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload(
                        reviewRequestTaskPayload,
                        RequestActionPayloadType.WASTE_QDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestActionPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.WASTE_QDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }

    @Transactional
    public void updateRequestPayload(final RequestTask requestTask,
                                     final AppUser appUser) {
        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload reviewRequestTaskPayload =
                (WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        WasteQDRRequestPayload requestPayload = (WasteQDRRequestPayload) request.getPayload();

        requestPayload.setRegulatorReviewer(appUser.getUserId());
        requestPayload.setReviewDecision(reviewRequestTaskPayload.getReviewDecision());
        requestPayload.setRegulatorReviewAttachments(reviewRequestTaskPayload.getRegulatorReviewAttachments());
        requestPayload.setRegulatorReviewSectionsCompleted(reviewRequestTaskPayload.getRegulatorReviewSectionsCompleted());
    }
}
