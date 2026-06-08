package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestCreateActionPayload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERReinitiateValidatorTest {

    @InjectMocks
    private NERReinitiateValidator cut;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Test
    void validateAction_whenValid_returnsValidResult() {

        Long accountId = 1L;

        NERRequestCreateActionPayload payload = NERRequestCreateActionPayload.builder()
                .requestId("REQ-1")
                .build();

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO("REQ-1",RequestType.NER, RequestStatus.COMPLETED, LocalDateTime.now(), null);
        when(requestQueryService.findRequestDetailsById("REQ-1"))
                .thenReturn(requestDetailsDTO);

        when(requestCreateValidatorService.validateAccountStatuses(
                accountId, Set.of(InstallationAccountStatus.LIVE)))
                .thenReturn(RequestCreateAccountStatusValidationResult.builder()
                        .valid(true)
                        .build());

        InstallationAccountDTO account = InstallationAccountDTO.builder()
                .emitterType(EmitterType.GHGE)
                .faStatus(false)
                .build();

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(account);

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of());

        RequestCreateValidationResult result = cut.validateAction(accountId, payload);

        assertThat(result.isValid()).isTrue();
        assertThat(result.isAvailable()).isTrue();
    }

    @Test
    void validateAction_whenRequestIsNotNER_throwsBusinessException() {

        Long accountId = 1L;

        NERRequestCreateActionPayload payload = NERRequestCreateActionPayload.builder()
                .requestId("REQ-1")
                .build();

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO("REQ-1",RequestType.BDR, null, LocalDateTime.now(), null);
        when(requestQueryService.findRequestDetailsById("REQ-1"))
                .thenReturn(requestDetailsDTO);

        assertThatThrownBy(() -> cut.validateAction(accountId, payload))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateAction_whenRequestNotCompleted_returnsInvalidResult() {

        Long accountId = 1L;

        NERRequestCreateActionPayload payload = NERRequestCreateActionPayload.builder()
                .requestId("REQ-1")
                .build();

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO("REQ-1",RequestType.NER, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);
        when(requestQueryService.findRequestDetailsById("REQ-1"))
                .thenReturn(requestDetailsDTO);

        when(requestCreateValidatorService.validateAccountStatuses(
                accountId, Set.of(InstallationAccountStatus.LIVE)))
                .thenReturn(RequestCreateAccountStatusValidationResult.builder()
                        .valid(true)
                        .build());

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder()
                        .emitterType(EmitterType.GHGE)
                        .faStatus(false)
                        .build());

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of());

        RequestCreateValidationResult result = cut.validateAction(accountId, payload);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes())
                .containsExactly(RequestType.NER);
    }

    @Test
    void validateAction_whenAccountStatusInvalid_returnsInvalidResult() {

        Long accountId = 1L;

        NERRequestCreateActionPayload payload = NERRequestCreateActionPayload.builder()
                .requestId("REQ-1")
                .build();

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO("REQ-1",RequestType.NER, RequestStatus.COMPLETED, LocalDateTime.now(), null);
        when(requestQueryService.findRequestDetailsById("REQ-1"))
                .thenReturn(requestDetailsDTO);

        when(requestCreateValidatorService.validateAccountStatuses(
                accountId, Set.of(InstallationAccountStatus.LIVE)))
                .thenReturn(RequestCreateAccountStatusValidationResult.builder()
                        .valid(false)
                        .reportedAccountStatus(InstallationAccountStatus.REVOKED)
                        .build());

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder()
                        .emitterType(EmitterType.GHGE)
                        .faStatus(false)
                        .build());

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of());

        RequestCreateValidationResult result = cut.validateAction(accountId, payload);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedAccountStatus())
                .isEqualTo(InstallationAccountStatus.REVOKED);
    }

    @Test
    void validateAction_whenNERNotAvailable_returnsNotAvailableResult() {

        Long accountId = 1L;

        NERRequestCreateActionPayload payload = NERRequestCreateActionPayload.builder()
                .requestId("REQ-1")
                .build();

        RequestDetailsDTO requestDetailsDTO = new RequestDetailsDTO("REQ-1",RequestType.NER, RequestStatus.COMPLETED, LocalDateTime.now(), null);
        when(requestQueryService.findRequestDetailsById("REQ-1"))
                .thenReturn(requestDetailsDTO);

        when(requestCreateValidatorService.validateAccountStatuses(
                accountId, Set.of(InstallationAccountStatus.LIVE)))
                .thenReturn(RequestCreateAccountStatusValidationResult.builder()
                        .valid(true)
                        .build());

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder()
                        .emitterType(EmitterType.HSE)
                        .faStatus(false)
                        .build());

        when(requestRepository.findByAccountIdAndType(accountId, RequestType.BDR))
                .thenReturn(List.of());

        RequestCreateValidationResult result = cut.validateAction(accountId, payload);

        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    void getType() {
        assertThat(cut.getType()).isEqualTo(RequestCreateActionType.NER_RE_INITIATE);
    }
}
