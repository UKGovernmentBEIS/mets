package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.mapper.AviationVirMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.validation.AviationVirCreationValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AviationVirCreationService {

    private final RequestService requestService;
    private final AviationVirDueDateService virDueDateService;
    private final AviationVirCreationValidator virCreationValidator;
    private final StartProcessRequestService startProcessRequestService;
    
    private static final AviationVirMapper VIR_MAPPER = Mappers.getMapper(AviationVirMapper.class);

    @Transactional
    public Request createRequestVir(final String aerRequestId) {
        
        final Request aerRequest = requestService.findRequestById(aerRequestId);
        final AviationAerRequestPayload aerRequestPayload = (AviationAerRequestPayload) aerRequest.getPayload();
        final AviationAerRequestMetadata aerRequestMetadata = (AviationAerRequestMetadata) aerRequest.getMetadata();

        // Initiate VIR request payload
        final AviationVirRequestPayload virRequestPayload = AviationVirRequestPayload.builder()
                .payloadType(RequestPayloadType.AVIATION_VIR_REQUEST_PAYLOAD)
                .verificationData(VIR_MAPPER
                        .toVirVerificationData(aerRequestPayload.getVerificationData()))
                .build();

        // Validate if VIR is allowed
        final RequestCreateValidationResult validationResult = 
            virCreationValidator.validate(
                virRequestPayload.getVerificationData(),
                aerRequest.getAccountId(), 
                aerRequestMetadata.getYear()
            );
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.VIR_CREATION_NOT_ALLOWED, validationResult);
        }

        // Add Expiration Date
        final Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.AVIATION_VIR_EXPIRATION_DATE,
                virDueDateService.generateDueDate(Year.of(aerRequest.getCreationDate().getYear())));

        // Start VIR flow
        final RequestParams params = RequestParams.builder()
                .type(RequestType.AVIATION_VIR)
                .accountId(aerRequest.getAccountId())
                .requestPayload(virRequestPayload)
                .requestMetadata(AviationVirRequestMetadata.builder()
                        .type(RequestMetadataType.AVIATION_VIR)
                        .year(aerRequestMetadata.getYear())
                        .relatedAerRequestId(aerRequest.getId())
                        .build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(params);
    }
}
