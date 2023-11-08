package uk.gov.pmrv.api.mireport.common.outstandingrequesttasks;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Set;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.NER_APPLICATION_PEER_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.NER_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.NER_AUTHORITY_RESPONSE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.NER_CONFIRM_PAYMENT;

@UtilityClass
public class RequestTaskTypeFilter {

    public boolean containsExcludedRequestTaskType(RequestTaskType requestTaskType) {
        Set<RequestTaskType> excludedRequestTaskTypes = RequestTaskType.getWaitForRequestTaskTypes();
        excludedRequestTaskTypes.addAll(RequestTaskType.getTrackPaymentTypes());
        //TODO: REMOVE WHEN NER IS IMPLEMENTED
        excludedRequestTaskTypes.addAll(List.of(NER_AUTHORITY_RESPONSE, NER_APPLICATION_PEER_REVIEW, NER_APPLICATION_REVIEW, NER_CONFIRM_PAYMENT));
        return excludedRequestTaskTypes.contains(requestTaskType);
    }
}
