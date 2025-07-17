package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.mapper.PermitVariationReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationAmendService {
    private final PermitReviewService permitReviewService;
    private final PermitVariationReviewMapper permitReviewMapper = Mappers.getMapper(PermitVariationReviewMapper.class);
    private final PermitValidatorService permitValidatorService;

    @Transactional
    public void amendPermitVariation(
        PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload permitSaveApplicationAmendRequestTaskActionPayload,
        RequestTask requestTask) {
        PermitVariationApplicationAmendsSubmitRequestTaskPayload
        requestTaskPayload = (PermitVariationApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setPermit(permitSaveApplicationAmendRequestTaskActionPayload.getPermit());
        requestTaskPayload.setPermitVariationDetails(permitSaveApplicationAmendRequestTaskActionPayload.getPermitVariationDetails());
        requestTaskPayload.setPermitSectionsCompleted(permitSaveApplicationAmendRequestTaskActionPayload.getPermitSectionsCompleted());
        requestTaskPayload.setPermitVariationDetailsCompleted(permitSaveApplicationAmendRequestTaskActionPayload.getPermitVariationDetailsCompleted());
        requestTaskPayload.setReviewSectionsCompleted(permitSaveApplicationAmendRequestTaskActionPayload.getReviewSectionsCompleted());
        requestTaskPayload.setPermitVariationDetailsReviewCompleted(permitSaveApplicationAmendRequestTaskActionPayload.getPermitVariationDetailsReviewCompleted());
        requestTaskPayload.setPermitVariationDetailsAmendCompleted(permitSaveApplicationAmendRequestTaskActionPayload.getPermitVariationDetailsAmendCompleted());
    }
	
	@Transactional
    public void submitAmendedPermitVariation(
        PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload submitApplicationAmendRequestTaskActionPayload,
        RequestTask requestTask) {
        Request request = requestTask.getRequest();
        PermitVariationApplicationAmendsSubmitRequestTaskPayload permitVariationApplicationAmendsSubmitRequestTaskPayload =
            (PermitVariationApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        PermitContainer permitContainer = permitReviewMapper.toPermitContainer(permitVariationApplicationAmendsSubmitRequestTaskPayload);
        permitValidatorService.validatePermit(permitContainer);

        PermitVariationRequestPayload permitVariationRequestPayload = (PermitVariationRequestPayload) request.getPayload();

        //PMRV-7723 : review decisions for dynamic sections are never removed from request payload when operator amends the permit
        //this should be done before setting the permit object of the requestPayload with the corresponding from the task payload
        permitReviewService.cleanUpDeprecatedReviewGroupDecisions(
            permitVariationRequestPayload,
            permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermit().getMonitoringApproaches()
                .getMonitoringApproaches().keySet());

        permitVariationRequestPayload.setPermitType(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitType());
        permitVariationRequestPayload.setPermit(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermit());
        permitVariationRequestPayload.setPermitAttachments(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitAttachments());
        permitVariationRequestPayload.setReviewSectionsCompleted(permitVariationApplicationAmendsSubmitRequestTaskPayload.getReviewSectionsCompleted());
        permitVariationRequestPayload.setPermitVariationDetails(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitVariationDetails());
        permitVariationRequestPayload.setPermitVariationDetailsCompleted(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitVariationDetailsCompleted());
        permitVariationRequestPayload.setPermitVariationDetailsReviewCompleted(permitVariationApplicationAmendsSubmitRequestTaskPayload.getPermitVariationDetailsReviewCompleted());
        
        permitVariationRequestPayload.setPermitSectionsCompleted(submitApplicationAmendRequestTaskActionPayload.getPermitSectionsCompleted());
    }
    
}
