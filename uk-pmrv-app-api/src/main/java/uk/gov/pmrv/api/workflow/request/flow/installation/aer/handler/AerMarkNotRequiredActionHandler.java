package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationMarkNotRequiredRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerMarkNotRequiredDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AerMarkNotRequiredActionHandler {

    private final RequestService requestService;

    private final WorkflowService workflowService;

    private final ReportableEmissionsService reportableEmissionsService;

    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);


    @Transactional
    public void process(final String requestId,
                        final AppUser appUser, AerMarkNotRequiredDetails markNotRequiredDetails) {

        final Map<String, Object> aerVariables = new HashMap<>();
        final Request request = requestService.findRequestById(requestId);

        AerApplicationMarkNotRequiredRequestActionPayload requestActionPayload = aerMapper
                .toAerApplicationMarkNotRequiredRequestActionPayload(markNotRequiredDetails);

        requestService.addActionToRequest(request,
                requestActionPayload,
                RequestActionType.AER_APPLICATION_NOT_REQUIRED,
                appUser.getUserId());

        AerRequestMetadata requestMetadata = (AerRequestMetadata) request.getMetadata();
        requestMetadata.setEmissions(null);

        ReportableEmissionsSaveParams emissionsSaveParams = ReportableEmissionsSaveParams.builder()
                        .accountId(request.getAccountId())
                        .year(requestMetadata.getYear())
                        .reportableEmissions(null)
                        .isFromDre(false)
                        .isFromRegulator(true)
                        .requestId(requestId)
                        .build();

        reportableEmissionsService.saveReportableEmissions(emissionsSaveParams);

        workflowService.sendEvent(requestId, BpmnProcessConstants.AER_MARK_NOT_REQUIRED, aerVariables);
    }
}
