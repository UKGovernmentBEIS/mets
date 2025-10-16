package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETICreateValidator;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HSETICreateActionHandlerTest {

    @InjectMocks
    private HSETICreateActionHandler hsetiCreateActionHandler;

    @Mock
    private HSETICreateValidator createValidator;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        final long accountId = 1L;
        final String requestId = "HSE_TI00001-2021";
        final String userId = "userId";
        final AppUser user = AppUser.builder().userId(userId).build();

        HSETIRequestPayload hsetiRequestPayload = HSETIRequestPayload.builder()
                .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
                .operatorAssignee(userId)
                .hseti(HSETI.builder()
                        .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                        .build()
                )
                .build();

        HSETIRequestMetadata hsetiRequestMetadata = HSETIRequestMetadata.builder()
                .type(RequestMetadataType.HSE_TI)
                .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                .build();

        Request request = Request
                .builder()
                .id(requestId)
                .payload(hsetiRequestPayload)
                .metadata(hsetiRequestMetadata)
                .build();

        HSETIRequestCreateActionPayload actionPayload = HSETIRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.HSE_TI_REQUEST_CREATE_ACTION_PAYLOAD)
                .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                .build();

        RequestCreateValidationResult requestCreateValidationResult = RequestCreateValidationResult.builder().valid(true).build();

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.HSE_TI)
                .accountId(accountId)
                .requestPayload(hsetiRequestPayload)
                .requestMetadata(hsetiRequestMetadata)
                .build();

        when(createValidator.validateAction(actionPayload, accountId)).thenReturn(requestCreateValidationResult);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);

        String actual = hsetiCreateActionHandler.process(accountId, actionPayload, user);

        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        Assertions.assertEquals(requestId, actual);
    }

    @Test
    void getRequestCreateActionType() {
        Assertions.assertEquals(RequestCreateActionType.HSE_TI, hsetiCreateActionHandler.getRequestCreateActionType());
    }
}
