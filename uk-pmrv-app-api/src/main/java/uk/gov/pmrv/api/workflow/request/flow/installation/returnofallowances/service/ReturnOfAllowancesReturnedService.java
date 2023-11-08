package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator.ReturnOfAllowancesReturnedValidator;

@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesReturnedService {

    private final RequestService requestService;
    private final ReturnOfAllowancesReturnedValidator returnOfAllowancesReturnedValidator;

    @Transactional
    public void applySavePayload(final ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload actionPayload,
                                 final RequestTask requestTask) {

        final ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload taskPayload =
            (ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
        taskPayload.setReturnOfAllowancesReturned(actionPayload.getReturnOfAllowancesReturned());
    }

    @Transactional
    public void submit(final RequestTask requestTask) {

        final Request request = requestTask.getRequest();
        final ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload taskPayload =
            (ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        returnOfAllowancesReturnedValidator.validate(taskPayload.getReturnOfAllowancesReturned());

        final ReturnOfAllowancesRequestPayload requestPayload =
            (ReturnOfAllowancesRequestPayload) request.getPayload();

        requestPayload.setReturnOfAllowancesReturnedSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setReturnOfAllowancesReturned(taskPayload.getReturnOfAllowancesReturned());

        final ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload actionPayload =
            ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED_PAYLOAD)
                .returnOfAllowancesReturned(requestPayload.getReturnOfAllowancesReturned())
                .build();
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED,
            request.getPayload().getRegulatorAssignee());
    }
}
