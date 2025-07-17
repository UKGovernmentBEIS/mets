package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.allowance.service.AllowanceActivityLevelService;
import uk.gov.pmrv.api.allowance.service.AllowanceAllocationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

@Service
@RequiredArgsConstructor
public class DoalAllowancesService {

    private final RequestService requestService;
    private final AllowanceActivityLevelService allowanceActivityLevelService;
    private final AllowanceAllocationService allowanceAllocationService;

    public void insertAllowanceValues(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();

        // Insert values
        switch (requestPayload.getDoalAuthority().getAuthorityResponse().getType()) {
            case VALID, VALID_WITH_CORRECTIONS -> insertAllowanceValuesForGranted(requestPayload, request.getAccountId());
            case INVALID -> insertAllowanceValuesForRejected(requestPayload, request.getAccountId());
        }
    }

    private void insertAllowanceValuesForGranted(DoalRequestPayload requestPayload, Long accountId) {
        final DoalGrantAuthorityResponse grantAuthorityResponse =
                (DoalGrantAuthorityResponse) requestPayload.getDoalAuthority().getAuthorityResponse();
        if(!grantAuthorityResponse.getPreliminaryAllocations().isEmpty()) {
            // Insert allocations to DB
            allowanceAllocationService.submitAllocations(grantAuthorityResponse.getPreliminaryAllocations(), accountId);
        }

        final ActivityLevelChangeInformation changeInformation = requestPayload.getDoal().getActivityLevelChangeInformation();
        if(!changeInformation.getActivityLevels().isEmpty()) {
            // Insert activity levels to DB
            allowanceActivityLevelService.submitActivityLevels(changeInformation.getActivityLevels(), accountId);
        }
    }

    private void insertAllowanceValuesForRejected(DoalRequestPayload requestPayload, Long accountId) {
        final ActivityLevelChangeInformation changeInformation = requestPayload.getDoal().getActivityLevelChangeInformation();
        if(!changeInformation.getPreliminaryAllocations().isEmpty()) {
            // Insert allocations to DB
            allowanceAllocationService.submitAllocations(changeInformation.getPreliminaryAllocations(), accountId);
        }

        if(!changeInformation.getActivityLevels().isEmpty()) {
            // Insert activity levels to DB
            allowanceActivityLevelService.submitActivityLevels(changeInformation.getActivityLevels(), accountId);
        }
    }
}
