package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferAValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferAttachmentsValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitTransferAValidatorServiceTest {

    @InjectMocks
    private PermitTransferAValidatorService validator;

    @Mock
    private PermitTransferAttachmentsValidator attachmentsValidator;
    
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;
    
    @Test
    void validateTaskPayload_whenAttachmentsMissing_thenException() {
        
        final UUID attachment = UUID.randomUUID();
        final PermitTransferAApplicationRequestTaskPayload payload =
            PermitTransferAApplicationRequestTaskPayload.builder()
                .permitTransferDetails(
                    PermitTransferDetails.builder()
                        .reason("the reason")
                        .reasonAttachments(Set.of(attachment))
                        .payer(TransferParty.TRANSFERER)
                        .transferCode("123456789")
                        .transferDate(LocalDate.of(2022, 1, 1))
                        .build()
                )
                .build();

        when(attachmentsValidator.attachmentsExist(Set.of(attachment))).thenReturn(false);

        final BusinessException businessException = assertThrows(BusinessException.class, () ->
            validator.validateTaskPayload(payload));

        assertEquals(ErrorCode.INVALID_PERMIT_TRANSFER, businessException.getErrorCode());
    }

    @Test
    void validatePermitTransferA_whenTransferBRequestExists_thenException() {

        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().accountId(1L).build())
            .payload(PermitTransferAApplicationRequestTaskPayload.builder()
                .permitTransferDetails(
                    PermitTransferDetails.builder()
                        .reason("the reason")
                        .payer(TransferParty.TRANSFERER)
                        .transferCode("123456789")
                        .transferDate(LocalDate.of(2022, 1, 1))
                        .build()
                )
                .build())
            .build();

        when(installationAccountQueryService.getByActiveTransferCode("123456789"))
            .thenReturn(Optional.of(InstallationAccountDTO.builder().id(23L).build()));
        when(requestRepository.findByAccountIdAndTypeAndStatus(23L, RequestType.PERMIT_TRANSFER_B, RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(Request.builder().id("request id").build()));
        
        final BusinessException businessException = assertThrows(BusinessException.class, () ->
            validator.validatePermitTransferA(requestTask));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
    }

    @Test
    void validatePermitTransferA_whenTransferCodeDisabled_thenException() {

        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().accountId(1L).build())
            .payload(PermitTransferAApplicationRequestTaskPayload.builder()
                .permitTransferDetails(
                    PermitTransferDetails.builder()
                        .reason("the reason")
                        .payer(TransferParty.TRANSFERER)
                        .transferCode("123456789")
                        .transferDate(LocalDate.of(2022, 1, 1))
                        .build()
                )
                .build())
            .build();

        when(installationAccountQueryService.getByActiveTransferCode("123456789")).thenReturn(Optional.empty());

        final BusinessException businessException = assertThrows(BusinessException.class, () ->
            validator.validatePermitTransferA(requestTask));

        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
    }
}
