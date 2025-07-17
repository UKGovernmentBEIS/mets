package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerCreationService {

    private final StartProcessRequestService startProcessRequestService;
    private final AerCreationValidatorService aerCreationValidatorService;
    private final AerDueDateService aerDueDateService;
    private final PermitQueryService permitQueryService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final AerInitializationService aerInitializationService;
    private final AerMonitoringPlanVersionsBuilderService aerMonitoringPlanVersionsBuilderService;
    private final AerPermitOriginatedDataBuilderService aerPermitOriginatedDataBuilderService;


    @Transactional
    public Request createRequestAer(Long accountId, RequestType initiationRequestType) {
        Year aerYear = Year.now().minusYears(1);

        // Validate if AER is allowed
        RequestCreateValidationResult validationResult = aerCreationValidatorService.validateAccountStatus(accountId);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.AER_CREATION_NOT_ALLOWED, validationResult);
        }

		return createRequestAerForYear(accountId, aerYear, aerDueDateService.generateDueDate(),
				AerInitiatorRequest.builder().type(initiationRequestType).build());
    }

    @Transactional
    public Request createRequestAerForYear(Long accountId, Year aerYear, Date expirationDate, AerInitiatorRequest initiatorRequest) {
        // Validate if AER is allowed
        RequestCreateValidationResult validationResult = aerCreationValidatorService.validateYear(accountId, aerYear);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.AER_CREATION_NOT_ALLOWED, validationResult);
        }

        // Create and start workflow
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.AER_EXPIRATION_DATE, expirationDate);
        return createRequestAer(accountId, aerYear, initiatorRequest, processVars);
    }

    private Request createRequestAer(Long accountId, Year aerYear, AerInitiatorRequest initiatorRequest, Map<String, Object> processVars) {
        final PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(accountId);
        final InstallationCategory installationCategory =
            installationOperatorDetailsQueryService.getInstallationCategory(accountId);
        final Aer aer = aerInitializationService.initializeAer(permitContainer.getPermit());
        final PermitOriginatedData permitOriginatedData = aerPermitOriginatedDataBuilderService
            .buildPermitOriginatedData(accountId, aer, permitContainer, installationCategory);
        String permitId = permitQueryService.getPermitIdByAccountId(accountId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        RequestParams params = RequestParams.builder()
                .type(RequestType.AER)
                .accountId(accountId)
                .requestPayload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .aer(aer)
                        .permitOriginatedData(permitOriginatedData)
                        .monitoringPlanVersions(aerMonitoringPlanVersionsBuilderService.buildMonitoringPlanVersions(accountId, permitId, aerYear))
                        .build())
                .requestMetadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .year(aerYear)
                        .initiatorRequest(initiatorRequest)
                        .build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(params);
    }

}
