package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.allowance.service.AllowanceActivityLevelService;
import uk.gov.pmrv.api.allowance.service.AllowanceAllocationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.GrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@Service
@RequiredArgsConstructor
public class NerAllowancesService {

    private final RequestService requestService;
    private final AllowanceActivityLevelService allowanceActivityLevelService;
    private final AllowanceAllocationService allowanceAllocationService;

    public void insertAllowanceValues(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();

        // Insert values
        switch (requestPayload.getAuthorityResponse().getType()) {
            case VALID, VALID_WITH_CORRECTIONS -> insertAllowanceValuesForGranted(requestPayload, request.getAccountId());
            case INVALID -> insertAllowanceValuesForRejected(requestPayload, request.getAccountId());
        }
    }

    private void insertAllowanceValuesForGranted(NerRequestPayload requestPayload, Long accountId) {
        final GrantAuthorityResponse grantAuthorityResponse =
                (GrantAuthorityResponse) requestPayload.getAuthorityResponse();
        if(!grantAuthorityResponse.getPreliminaryAllocations().isEmpty()) {
            // Insert allocations to DB
            allowanceAllocationService.submitAllocations(grantAuthorityResponse.getPreliminaryAllocations(), accountId);
        }

        final NerProceedToAuthorityDetermination authorityDetermination =
                (NerProceedToAuthorityDetermination) requestPayload.getDetermination();
        if(!authorityDetermination.getActivityLevels().isEmpty()) {
            // Insert activity levels to DB
            allowanceActivityLevelService.submitActivityLevels(authorityDetermination.getActivityLevels(), accountId);
        }
    }

    private void insertAllowanceValuesForRejected(NerRequestPayload requestPayload, Long accountId) {
        final NerProceedToAuthorityDetermination authorityDetermination =
                (NerProceedToAuthorityDetermination) requestPayload.getDetermination();
        if(!authorityDetermination.getPreliminaryAllocations().isEmpty()) {
            // Insert allocations to DB
            allowanceAllocationService.submitAllocations(authorityDetermination.getPreliminaryAllocations(), accountId);
        }

        if(!authorityDetermination.getActivityLevels().isEmpty()) {
            // Insert activity levels to DB
            allowanceActivityLevelService.submitActivityLevels(authorityDetermination.getActivityLevels(), accountId);
        }
    }
}
