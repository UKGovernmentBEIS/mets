package uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationAlreadyPerformedRequestTaskActionValidatorTest {
    @InjectMocks
    private VerificationAlreadyPerformedRequestTaskActionValidator validator;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void getErrorMessage() {
        assertThat(validator.getErrorMessage()).isEqualTo(RequestTaskActionValidationResult.ErrorMessage.VERIFIED_DATA_FOUND);
    }

    @Test
    void getTypes() {
        assertThat(validator.getTypes()).isEqualTo(Set.of(RequestTaskActionType.AER_REQUEST_VERIFICATION,
                RequestTaskActionType.AER_REQUEST_AMENDS_VERIFICATION));
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(Set.of(), validator.getConflictingRequestTaskTypes());
    }

    @Test
    void validate_valid() {
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .payload(AerApplicationSubmitRequestTaskPayload.builder().verificationPerformed(false).build())
                .build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L))
                .thenReturn(InstallationAccountInfoDTO.builder().emitterType(EmitterType.GHGE).build());

        assertEquals(RequestTaskActionValidationResult.validResult(), validator.validate(requestTask));


        verify(installationAccountQueryService, times(1))
                .getInstallationAccountInfoDTOById(1L);
    }

    @Test
    void validate_invalid() {
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .payload(AerApplicationSubmitRequestTaskPayload.builder().verificationPerformed(true).build())
                .build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(1L))
                .thenReturn(InstallationAccountInfoDTO.builder().emitterType(EmitterType.GHGE).build());

        assertEquals(RequestTaskActionValidationResult.invalidResult(RequestTaskActionValidationResult.ErrorMessage.VERIFIED_DATA_FOUND),
                validator.validate(requestTask));


        verify(installationAccountQueryService, times(1))
                .getInstallationAccountInfoDTOById(1L);
    }
}