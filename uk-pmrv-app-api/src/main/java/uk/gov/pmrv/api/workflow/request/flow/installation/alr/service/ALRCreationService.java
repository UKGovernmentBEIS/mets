package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRCreationValidationService;

import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ALRCreationService {

    private final ALRCreationValidationService alrCreationValidatorService;
    private final ALRDueDateService alrDueDateService;
    private final DateService dateService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public Request createALR(Long accountId) {
        Year alrYear = dateService.getYear();

        // Validate if ALR is allowed
        RequestCreateValidationResult validationResult = alrCreationValidatorService.validateAccountStatus(accountId);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.ALR_CREATION_NOT_ALLOWED, validationResult);
        }

        return createALRForYear(accountId, alrYear, alrDueDateService.generateDueDate());
    }

    @Transactional
    public Request createALRForYear(Long accountId, Year alrYear, Date expirationDate) {
        // Validate if ALR is allowed
        RequestCreateValidationResult yearValidationYearResult = alrCreationValidatorService.validateYear(accountId, alrYear);
        if(!yearValidationYearResult.isValid()) {
            throw new BusinessException(MetsErrorCode.ALR_CREATION_NOT_ALLOWED, yearValidationYearResult);
        }

        RequestCreateValidationResult emitterTypeAndFAValidationYearResult = alrCreationValidatorService.validateAccountEmitterTypeAndFreeAllocations(accountId);
        if(!emitterTypeAndFAValidationYearResult.isValid()) {
            throw new BusinessException(MetsErrorCode.ALR_CREATION_NOT_ALLOWED, emitterTypeAndFAValidationYearResult);
        }

        // Create and start workflow
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.ALR_EXPIRATION_DATE, expirationDate);
        return createALR(accountId, alrYear, processVars);
    }

    private Request createALR(Long accountId, Year alrYear, Map<String, Object> processVars) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.ALR)
                .accountId(accountId)
                .requestPayload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .alr(ALR.builder().build())
                        .build())
                .requestMetadata(ALRRequestMetaData.builder()
                        .type(RequestMetadataType.ALR)
                        .year(alrYear)
                        .build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(requestParams);
    }
}
