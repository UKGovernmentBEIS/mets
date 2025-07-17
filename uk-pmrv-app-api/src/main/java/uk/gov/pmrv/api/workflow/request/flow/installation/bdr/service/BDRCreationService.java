package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BDRCreationService {

    private final BDRCreationValidationService bdrCreationValidatorService;
    private final BDRDueDateService bdrDueDateService;
    private final DateService dateService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public Request createBDR(Long accountId) {
        Year bdrYear = dateService.getYear();

        // Validate if BDR is allowed
        RequestCreateValidationResult validationResult = bdrCreationValidatorService.validateAccountStatus(accountId);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.BDR_CREATION_NOT_ALLOWED, validationResult);
        }

		return createBDRForYear(accountId, bdrYear, bdrDueDateService.generateDueDate());
    }

    @Transactional
    public Request createBDRForYear(Long accountId, Year bdrYear, Date expirationDate) {
        // Validate if BDR is allowed
        RequestCreateValidationResult validationResult = bdrCreationValidatorService.validateYear(accountId, bdrYear);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.BDR_CREATION_NOT_ALLOWED, validationResult);
        }

        // Create and start workflow
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.BDR_EXPIRATION_DATE, expirationDate);
        processVars.put(BpmnProcessConstants.BDR_INITIATION_TYPE, BDRInitiationType.INITIATED);
        return createBDR(accountId, bdrYear, processVars);
    }

    private Request createBDR(Long accountId, Year bdrYear, Map<String, Object> processVars) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.BDR)
                .accountId(accountId)
                .requestPayload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .bdr(BDR.builder().build())
                        .build())
                .requestMetadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .bdrInitiationType(BDRInitiationType.INITIATED)
                        .year(bdrYear)
                        .build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(requestParams);
    }

}
