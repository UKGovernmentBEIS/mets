package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HSETICreateValidatorTest {

    @InjectMocks
    private HSETICreateValidator validator;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestCreateAccountRelatedValidator requestCreateAccountRelatedValidator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void validateAction_accountIsNotHSE_returnFalseValidationResult() {

        final Long accountId = 1L;
        InstallationAccountDTO accountInfo = InstallationAccountDTO
                .builder()
                .emitterType(EmitterType.GHGE)
                .build();

        when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(accountInfo);

        final RequestCreateValidationResult actual = validator.validateAction(accountId);

        assertThat(actual.isAvailable()).isFalse();
        verifyNoInteractions(requestCreateAccountRelatedValidator);
        verifyNoInteractions(requestCreateValidatorService);
        verifyNoInteractions(requestRepository);
        verify(installationAccountQueryService, times(1)).getAccountDTOById(accountId);
    }

    @Test
    void validateAction_aRequestExistsForEveryAllocationPeriod_returnFalseValidationResult() {
        final Long accountId = 1L;
        final String requestId = "HSE_TI00001-2021";

        InstallationAccountDTO accountInfo = InstallationAccountDTO
                .builder()
                .emitterType(EmitterType.HSE)
                .build();

        List<Request> requests = new ArrayList<>();

        for (HSETIAllocationPeriod allocationPeriod : HSETIAllocationPeriod.values()) {

            HSETIRequestPayload currPayload = HSETIRequestPayload.builder()
                .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
                .hseti(HSETI.builder()
                        .allocationPeriod(allocationPeriod)
                        .build()
                )
                .build();

            HSETIRequestMetadata currMetadata = HSETIRequestMetadata.builder()
                .type(RequestMetadataType.HSE_TI)
                .allocationPeriod(allocationPeriod)
                .build();

            requests.add(Request
                .builder()
                .id(requestId)
                .payload(currPayload)
                .metadata(currMetadata)
                .build());
        }

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.HSE_TI, RequestStatus.IN_PROGRESS)).thenReturn(requests);
        when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(accountInfo);

        final RequestCreateValidationResult actual = validator.validateAction(accountId);

        assertThat(actual.isValid()).isFalse();
        verifyNoInteractions(requestCreateAccountRelatedValidator);
        verifyNoInteractions(requestCreateValidatorService);
        verify(installationAccountQueryService, times(1)).getAccountDTOById(accountId);
    }

    @Test
    void validateAction_aRequestDoesNotExistForEveryAllocationPeriod_returnValidationResult() {
        final Long accountId = 1L;
        final String requestId = "HSE_TI00001-2021";


        InstallationAccountDTO accountInfo = InstallationAccountDTO
                .builder()
                .emitterType(EmitterType.HSE)
                .build();

        HSETIRequestPayload payload = HSETIRequestPayload.builder()
                .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
                .hseti(HSETI.builder()
                        .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                        .build()
                )
                .build();


        HSETIRequestMetadata metadata = HSETIRequestMetadata.builder()
                .type(RequestMetadataType.HSE_TI)
                .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                .build();

        Request request = Request
                .builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.HSE_TI, RequestStatus.IN_PROGRESS)).thenReturn(List.of(request));
        when(requestCreateValidatorService.validate(accountId,
                validator.getApplicableAccountStatuses(), 
                validator.getMutuallyExclusiveRequests())).thenReturn(RequestCreateValidationResult
                .builder()
                .valid(true)
                .build());
        when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(accountInfo);


        final RequestCreateValidationResult actual = validator.validateAction(accountId);

        assertThat(actual.isAvailable()).isTrue();
        verify(requestCreateValidatorService, times(1)).validate(accountId,
                validator.getApplicableAccountStatuses(), 
                validator.getMutuallyExclusiveRequests());
        verify(installationAccountQueryService, times(1)).getAccountDTOById(accountId);
    }

    @Test
    void validateAction_withPayload() {
        final Long accountId = 1L;
        final String requestId = "HSE_TI00001-2021";

        HSETIRequestPayload hsetiRequestPayload = HSETIRequestPayload.builder()
                .payloadType(RequestPayloadType.HSE_TI_REQUEST_PAYLOAD)
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

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.HSE_TI, RequestStatus.IN_PROGRESS)).thenReturn(List.of(request));

        HSETIRequestCreateActionPayload actionPayload = HSETIRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.HSE_TI_REQUEST_CREATE_ACTION_PAYLOAD)
                .allocationPeriod(HSETIAllocationPeriod.PERIOD_2021_2025)
                .build();

        final RequestCreateValidationResult actual = validator.validateAction(actionPayload, accountId);
        assertThat(actual.isValid()).isFalse();
    }

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.HSE_TI);
    }

    @Test
    void getApplicableAccountStatuses() {
        Set<AccountStatus> applicableAccountStatuses = validator.getApplicableAccountStatuses();

        Set<AccountStatus> expectedSet = Set.of(InstallationAccountStatus.LIVE);
        assertEquals(expectedSet, applicableAccountStatuses);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        Set<RequestType> mutuallyExclusiveRequests = validator.getMutuallyExclusiveRequests();

        assertEquals(Collections.emptySet(), mutuallyExclusiveRequests);
    }
}
