package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAuthorityReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRActivityLevel;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRGrantAuthorityWithCorrectionsResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ALRAllowancesServiceTest {

    @InjectMocks
    private ALRAllowancesService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AllowanceActivityLevelService allowanceActivityLevelService;

    @Mock
    private AllowanceAllocationService allowanceAllocationService;

    @Test
    void insertAllowanceValues_for_accepted() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final Set<ALRPreliminaryAllocation> allocations = Set.of(
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.now())
                        .allowances(10)
                        .build(),
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final Set<ALRPreliminaryAllocation> regulatorAllocations = Set.of(
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.CARBON_BLACK)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final List<ALRActivityLevel> activityLevels = List.of(
                ALRActivityLevel.builder()
                        .year(Year.now())
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .build()
        );

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.ALR)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().authorityResponse(ALRGrantAuthorityWithCorrectionsResponse.builder()
                                    .type(DoalAuthorityResponseType.VALID_WITH_CORRECTIONS)
                                    .preliminaryAllocations(new TreeSet<>(allocations))
                                    .build())
                                .build())
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().activityLevels(activityLevels)
                                .allocations(allocations).build())
                        .build())
                .accountId(accountId)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.insertAllowanceValues(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(allowanceAllocationService, times(1)).submitAllocations(allocations.stream()
                .map(pa -> (PreliminaryAllocation) pa)
                .collect(Collectors.toSet()), accountId);
        verify(allowanceActivityLevelService, times(1)).submitActivityLevels(activityLevels.stream()
                .map(al -> (ActivityLevel) al)
                .collect(Collectors.toList()), accountId);
    }

    @Test
    void insertAllowanceValues_for_rejected() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final Set<ALRPreliminaryAllocation> allocations = Set.of(
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.now())
                        .allowances(10)
                        .build(),
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final List<ALRActivityLevel> activityLevels = List.of(
                ALRActivityLevel.builder()
                        .year(Year.now())
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .build()
        );

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.ALR)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().authorityResponse(ALRGrantAuthorityWithCorrectionsResponse.builder()
                                        .type(DoalAuthorityResponseType.INVALID)
                                        .preliminaryAllocations(new TreeSet<>(allocations))
                                        .build())
                                .build())
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().activityLevels(activityLevels)
                                .allocations(allocations).build())
                        .build())
                .accountId(accountId)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.insertAllowanceValues(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(allowanceAllocationService, times(1)).submitAllocations(allocations.stream()
                .map(pa -> (PreliminaryAllocation) pa)
                .collect(Collectors.toSet()), accountId);
        verify(allowanceActivityLevelService, times(1)).submitActivityLevels(activityLevels.stream()
                .map(al -> (ActivityLevel) al)
                .collect(Collectors.toList()), accountId);
    }

    @Test
    void insertAllowanceValues_empty_values() {
        final String requestId = "requestId";
        final Long accountId = 1L;

        final Set<ALRPreliminaryAllocation> regulatorAllocations = Set.of(
                ALRPreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.CARBON_BLACK)
                        .year(Year.now())
                        .allowances(20)
                        .build()
        );
        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.ALR)
                .payload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().authorityResponse(ALRGrantAuthorityWithCorrectionsResponse.builder()
                                        .type(DoalAuthorityResponseType.VALID)
                                        .preliminaryAllocations(new TreeSet<>())
                                        .build())
                                .build())
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder()
                                .allocations(regulatorAllocations).build())
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
