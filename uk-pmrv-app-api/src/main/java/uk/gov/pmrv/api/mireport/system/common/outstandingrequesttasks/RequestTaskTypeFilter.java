package uk.gov.pmrv.api.mireport.system.common.outstandingrequesttasks;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Set;

@UtilityClass
public class RequestTaskTypeFilter {

    public boolean containsExcludedRequestTaskType(RequestTaskType requestTaskType) {
        Set<RequestTaskType> excludedRequestTaskTypes = RequestTaskType.getWaitForRequestTaskTypes();
        excludedRequestTaskTypes.addAll(RequestTaskType.getTrackPaymentTypes());
        excludedRequestTaskTypes.addAll(List.of());
        return excludedRequestTaskTypes.contains(requestTaskType);
    }
}
