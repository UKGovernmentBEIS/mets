package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper.NonComplianceMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceDailyPenaltyNoticeInitializer implements InitializeRequestTaskHandler {
    
    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);
    
    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        return NON_COMPLIANCE_MAPPER.toNonComplianceDailyPenaltyNoticeRequestTaskPayload(
            requestPayload, 
            RequestTaskPayloadType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE,
                      RequestTaskType.AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE);
    }
}
