package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.ActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.service.AllowanceActivityLevelService;
import uk.gov.pmrv.api.allowance.service.AllowanceAllocationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.GrantAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.RejectAuthorityResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.AuthorityResponseType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;

import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NerAllowancesServiceTest {

    @InjectMocks
    private NerAllowancesService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AllowanceActivityLevelService allowanceActivityLevelService;

    @Mock
    private AllowanceAllocationService allowanceAllocationService;

    @Test
    void insertAllowanceValues_for_valid() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.now())
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final Set<PreliminaryAllocation> regulatorAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.CARBON_BLACK)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final List<ActivityLevel> activityLevels = List.of(
                ActivityLevel.builder()
                        .year(Year.now())
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .build()
        );

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.NER)
                .payload(NerRequestPayload.builder()
                        .payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD)
                        .authorityResponse(GrantAuthorityResponse.builder()
                                .type(AuthorityResponseType.VALID)
                                .preliminaryAllocations(new TreeSet<>(allocations))
                                .build())
                        .determination(NerProceedToAuthorityDetermination.builder()
                                .type(NerDeterminationType.PROCEED_TO_AUTHORITY)
                                .preliminaryAllocations(new TreeSet<>(regulatorAllocations))
                                .activityLevels(activityLevels)
                                .build())
                        .build())
                .accountId(accountId)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.insertAllowanceValues(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(allowanceAllocationService, times(1)).submitAllocations(allocations, accountId);
        verify(allowanceActivityLevelService, times(1)).submitActivityLevels(activityLevels, accountId);
    }

    @Test
    void insertAllowanceValues_for_invalid() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.now())
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final List<ActivityLevel> activityLevels = List.of(
                ActivityLevel.builder()
                        .year(Year.now())
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .build()
        );

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.NER)
                .payload(NerRequestPayload.builder()
                        .payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD)
                        .authorityResponse(RejectAuthorityResponse.builder()
                                .type(AuthorityResponseType.INVALID)
                                .build())
                        .determination(NerProceedToAuthorityDetermination.builder()
                                .type(NerDeterminationType.PROCEED_TO_AUTHORITY)
                                .preliminaryAllocations(new TreeSet<>(allocations))
                                .activityLevels(activityLevels)
                                .build())
                        .build())
                .accountId(accountId)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.insertAllowanceValues(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(allowanceAllocationService, times(1)).submitAllocations(allocations, accountId);
        verify(allowanceActivityLevelService, times(1)).submitActivityLevels(activityLevels, accountId);
    }

    @Test
    void insertAllowanceValues_empty_values() {
        final String requestId = "requestId";
        final Long accountId = 1L;

        final Set<PreliminaryAllocation> regulatorAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.CARBON_BLACK)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.NER)
                .payload(NerRequestPayload.builder()
                        .payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD)
                        .authorityResponse(GrantAuthorityResponse.builder()
                                .type(AuthorityResponseType.VALID)
                                .preliminaryAllocations(new TreeSet<>())
                                .build())
                        .determination(NerProceedToAuthorityDetermination.builder()
                                .type(NerDeterminationType.PROCEED_TO_AUTHORITY)
                                .preliminaryAllocations(new TreeSet<>(regulatorAllocations))
                                .activityLevels(List.of())
                                .build())
                        .build())
                .accountId(accountId)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.insertAllowanceValues(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(allowanceAllocationService, allowanceActivityLevelService);
    }
}
