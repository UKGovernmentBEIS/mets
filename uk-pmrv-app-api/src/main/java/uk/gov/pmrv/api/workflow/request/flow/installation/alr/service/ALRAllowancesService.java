package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.service.AllowanceActivityLevelService;
import uk.gov.pmrv.api.allowance.service.AllowanceAllocationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRActivityLevel;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRGrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ALRAllowancesService {

    private final RequestService requestService;
    private final AllowanceActivityLevelService allowanceActivityLevelService;
    private final AllowanceAllocationService allowanceAllocationService;

    public void insertAllowanceValues(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        // Insert values
        switch (requestPayload.getAuthorityReviewOutcome().getAuthorityResponse().getType()) {
            case VALID, VALID_WITH_CORRECTIONS -> insertAllowanceValuesForGranted(requestPayload, request.getAccountId());
            case INVALID -> insertAllowanceValuesForRejected(requestPayload, request.getAccountId());
        }
    }

    private void insertAllowanceValuesForGranted(ALRRequestPayload requestPayload, Long accountId) {
        final ALRGrantAuthorityResponse grantAuthorityResponse =
                (ALRGrantAuthorityResponse) requestPayload.getAuthorityReviewOutcome().getAuthorityResponse();
        if(!grantAuthorityResponse.getPreliminaryAllocations().isEmpty()) {
            // Insert allocations to DB
            allowanceAllocationService.submitAllocations(grantAuthorityResponse.getPreliminaryAllocations().stream()
                    .map(pa -> (PreliminaryAllocation) pa)
                    .collect(Collectors.toSet()), accountId);
        }

        List<ALRActivityLevel> activityLevels = requestPayload.getRegulatorReviewOutcome().getActivityLevels();
        if(!activityLevels.isEmpty()) {
            // Insert activity levels to DB
            allowanceActivityLevelService.submitActivityLevels(activityLevels.stream()
                    .map(al -> (ActivityLevel) al)
                    .collect(Collectors.toList()), accountId);
        }
    }

    private void insertAllowanceValuesForRejected(ALRRequestPayload requestPayload, Long accountId) {
       Set<ALRPreliminaryAllocation> alrPreliminaryAllocations = requestPayload.getRegulatorReviewOutcome().getAllocations();
        if(!alrPreliminaryAllocations.isEmpty()) {
            // Insert allocations to DB
            allowanceAllocationService.submitAllocations(alrPreliminaryAllocations.stream()
                    .map(pa -> (PreliminaryAllocation) pa)
                    .collect(Collectors.toSet()), accountId);
        }

        List<ALRActivityLevel> activityLevels = requestPayload.getRegulatorReviewOutcome().getActivityLevels();
        if(!activityLevels.isEmpty()) {
            // Insert activity levels to DB
            allowanceActivityLevelService.submitActivityLevels(activityLevels.stream()
                    .map(al -> (ActivityLevel) al)
                    .collect(Collectors.toList()), accountId);
        }
    }
}
