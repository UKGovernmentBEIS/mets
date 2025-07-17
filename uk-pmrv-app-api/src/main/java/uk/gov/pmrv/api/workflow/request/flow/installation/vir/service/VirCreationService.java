package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.mapper.VirMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.validation.VirCreationValidatorService;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VirCreationService {

    private final RequestService requestService;
    private final VirDueDateService virDueDateService;
    private final VirCreationValidatorService virCreationValidatorService;
    private final StartProcessRequestService startProcessRequestService;
    private static final VirMapper virMapper = Mappers.getMapper(VirMapper.class);

    @Transactional
    public Request createRequestVir(String aerRequestId) {
        Request aerRequest = requestService.findRequestById(aerRequestId);
        AerRequestPayload aerRequestPayload = (AerRequestPayload) aerRequest.getPayload();
        AerRequestMetadata aerRequestMetadata = (AerRequestMetadata) aerRequest.getMetadata();
        InstallationCategory installationCategory = aerRequestPayload.getPermitOriginatedData().getInstallationCategory();

        // Initiate VIR request payload
        VirRequestPayload virRequestPayload = VirRequestPayload.builder()
                .payloadType(RequestPayloadType.VIR_REQUEST_PAYLOAD)
                .verificationData(virMapper
                        .toVirVerificationData(aerRequestPayload.getVerificationReport().getVerificationData(), installationCategory))
                .build();

        // Validate if VIR is allowed
        RequestCreateValidationResult validationResult = virCreationValidatorService.validate(virRequestPayload.getVerificationData(),
                aerRequest.getAccountId(), aerRequestMetadata.getYear(), aerRequestPayload.getPermitOriginatedData().getPermitType());
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.VIR_CREATION_NOT_ALLOWED, validationResult);
        }

        // Add Expiration Date
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.VIR_EXPIRATION_DATE,
                virDueDateService.generateDueDate(Year.of(aerRequest.getCreationDate().getYear())));

        // Start VIR flow
        RequestParams params = RequestParams.builder()
                .type(RequestType.VIR)
                .accountId(aerRequest.getAccountId())
                .requestPayload(virRequestPayload)
                .requestMetadata(VirRequestMetadata.builder()
                        .type(RequestMetadataType.VIR)
                        .year(aerRequestMetadata.getYear())
                        .relatedAerRequestId(aerRequest.getId())
                        .build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(params);
    }
}
