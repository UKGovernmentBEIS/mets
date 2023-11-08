package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.pmrv.api.allowance.service.AllowanceQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalApplicationSubmitInitializerTest {

    @InjectMocks
    private DoalApplicationSubmitInitializer initializer;

    @Mock
    private AllowanceQueryService allowanceQueryService;

    @Test
    void initializePayload() {
        final Long accountId = 1L;
        final Request request = Request.builder()
                .type(RequestType.DOAL)
                .accountId(accountId)
                .payload(DoalRequestPayload.builder().build())
                .build();

        final LocalDateTime creationDate = LocalDateTime.now();
        final List<HistoricalActivityLevel> historicalActivityLevels = List.of(
                HistoricalActivityLevel.builder()
                        .year(Year.of(2020))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .creationDate(creationDate)
                        .build()
        );
        final Set<PreliminaryAllocation> preliminaryAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build()
        );

        when(allowanceQueryService.getHistoricalActivityLevelsByAccount(accountId)).thenReturn(historicalActivityLevels);
        when(allowanceQueryService.getPreliminaryAllocationsByAccount(accountId)).thenReturn(preliminaryAllocations);

        // Invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertEquals(requestTaskPayload, DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .historicalActivityLevels(historicalActivityLevels)
                .historicalPreliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                .doal(Doal.builder()
                        .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                .preliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                                .build())
                        .build())
                .build());
        verify(allowanceQueryService, times(1)).getHistoricalActivityLevelsByAccount(accountId);
        verify(allowanceQueryService, times(1)).getPreliminaryAllocationsByAccount(accountId);
    }

    @Test
    void initializePayload_with_exist_doal() {
        final Long accountId = 1L;
        final Doal doal = Doal.builder()
                .additionalDocuments(DoalAdditionalDocuments.builder()
                        .exist(false)
                        .build())
                .build();
        final Request request = Request.builder()
                .type(RequestType.DOAL)
                .accountId(accountId)
                .payload(DoalRequestPayload.builder()
                        .doal(doal)
                        .build())
                .build();

        final LocalDateTime creationDate = LocalDateTime.now();
        final List<HistoricalActivityLevel> historicalActivityLevels = List.of(
                HistoricalActivityLevel.builder()
                        .year(Year.of(2020))
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .changeType(ChangeType.DECREASE)
                        .changedActivityLevel("-1%")
                        .comments("Comments")
                        .creationDate(creationDate)
                        .build()
        );
        final Set<PreliminaryAllocation> preliminaryAllocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2020))
                        .allowances(10)
                        .build()
        );

        when(allowanceQueryService.getHistoricalActivityLevelsByAccount(accountId)).thenReturn(historicalActivityLevels);
        when(allowanceQueryService.getPreliminaryAllocationsByAccount(accountId)).thenReturn(preliminaryAllocations);

        // Invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertEquals(requestTaskPayload, DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                .historicalActivityLevels(historicalActivityLevels)
                .historicalPreliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                .doal(doal)
                .build());
        verify(allowanceQueryService, times(1)).getHistoricalActivityLevelsByAccount(accountId);
        verify(allowanceQueryService, times(1)).getPreliminaryAllocationsByAccount(accountId);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.DOAL_APPLICATION_SUBMIT);
    }
}
