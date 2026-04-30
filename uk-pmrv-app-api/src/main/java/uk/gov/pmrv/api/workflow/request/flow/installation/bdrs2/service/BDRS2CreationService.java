package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2InitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import java.time.Year;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BDRS2CreationService {

    private final BDRS2CreationValidationService bdrs2CreationValidatorService;
    private final BDRS2DueDateService bdrs2DueDateService;
    private final DateService dateService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Request createBDRS2(Long accountId) {
        Year bdrs2Year = dateService.getYear();

        // Validate if BDRS2 is allowed
        RequestCreateValidationResult validationResult = bdrs2CreationValidatorService.validateAccountStatus(accountId);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.BDRS2_CREATION_NOT_ALLOWED, validationResult);
        }

        return createBDRS2ForYear(accountId, bdrs2Year, bdrs2DueDateService.generateDueDate());
    }

    private Request createBDRS2ForYear(Long accountId, Year bdrs2Year, Date expirationDate) {
        // Validate if BDRS2 is allowed
        RequestCreateValidationResult validationResult = bdrs2CreationValidatorService.validateYear(accountId, bdrs2Year);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.BDRS2_CREATION_NOT_ALLOWED, validationResult);
        }

        // Create and start workflow
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.BDRS2_EXPIRATION_DATE, expirationDate);
        processVars.put(BpmnProcessConstants.BDRS2_INITIATION_TYPE, BDRS2InitiationType.INITIATED);
        return createBDRS2(accountId, bdrs2Year, processVars);
    }

    private Request createBDRS2(Long accountId, Year bdrs2Year, Map<String, Object> processVars) {

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.BDRS2)
                .accountId(accountId)
                .requestPayload(BDRS2RequestPayload.builder()
                        .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                        .bdrs2(BDRS2.builder().build())
                        .build())
                .requestMetadata(BDRS2RequestMetadata.builder()
                        .type(RequestMetadataType.BDRS2)
                        .bdrs2InitiationType(BDRS2InitiationType.INITIATED)
                        .year(bdrs2Year)
                        .build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(requestParams);
    }
}
