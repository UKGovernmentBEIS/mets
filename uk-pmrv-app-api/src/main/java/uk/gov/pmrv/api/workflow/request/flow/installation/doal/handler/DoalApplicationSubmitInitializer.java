package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.allowance.domain.HistoricalActivityLevel;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.service.AllowanceQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.ActivityLevelChangeInformation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.mapper.DoalMapper;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class DoalApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    private final AllowanceQueryService allowanceQueryService;
    private static final DoalMapper DOAL_MAPPER = Mappers.getMapper(DoalMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final DoalRequestPayload requestPayload = (DoalRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        // Get historical data
        final List<HistoricalActivityLevel> historicalActivityLevels = allowanceQueryService
                .getHistoricalActivityLevelsByAccount(accountId);
        final Set<PreliminaryAllocation> preliminaryAllocations = allowanceQueryService
                .getPreliminaryAllocationsByAccount(accountId);

        // Return task payload
        if (doalExists(requestPayload)) {
            return DOAL_MAPPER.toDoalApplicationSubmitRequestTaskPayload(requestPayload,
                    RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD, historicalActivityLevels, preliminaryAllocations);
        }
        else{
            return DoalApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_SUBMIT_PAYLOAD)
                    .historicalActivityLevels(historicalActivityLevels)
                    .historicalPreliminaryAllocations(new TreeSet<>(preliminaryAllocations))
                    .doal(initializeDoalData(preliminaryAllocations))
                    .build();
        }
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.DOAL_APPLICATION_SUBMIT);
    }

    private boolean doalExists(DoalRequestPayload requestPayload) {
        return requestPayload.getDoal() != null;
    }

    private Doal initializeDoalData(Set<PreliminaryAllocation> allocations) {
        return Doal.builder()
                .activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                        .preliminaryAllocations(new TreeSet<>(allocations))
                        .build())
                .build();
    }
}
