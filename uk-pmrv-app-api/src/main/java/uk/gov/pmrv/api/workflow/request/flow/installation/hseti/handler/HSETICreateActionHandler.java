package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETI;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETICreateValidator;

@Component
@RequiredArgsConstructor
public class HSETICreateActionHandler implements RequestAccountCreateActionHandler<HSETIRequestCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final HSETICreateValidator createValidator;

    @Override
    public String process(Long accountId,
                          HSETIRequestCreateActionPayload payload, AppUser appUser) {

        //check if another HSE in progress has already this period
        RequestCreateValidationResult validationResult = createValidator.validateAction(payload, accountId);

        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.HSE_TI_ALLOCATION_PERIOD_IS_OPEN, validationResult);
        }

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.HSE_TI)
                .accountId(accountId)
                .requestPayload(HSETIRequestPayload.builder()
                        .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
                        .hseti(HSETI.builder()
                                .allocationPeriod(payload.getAllocationPeriod())
                                .build())
                        .build())
                .requestMetadata(HSETIRequestMetadata.builder()
                        .type(RequestMetadataType.HSE_TI)
                        .allocationPeriod(payload.getAllocationPeriod())
                        .build())

                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }


    @Override
    public RequestCreateActionType getRequestCreateActionType() {
        return RequestCreateActionType.HSE_TI;
    }

}
