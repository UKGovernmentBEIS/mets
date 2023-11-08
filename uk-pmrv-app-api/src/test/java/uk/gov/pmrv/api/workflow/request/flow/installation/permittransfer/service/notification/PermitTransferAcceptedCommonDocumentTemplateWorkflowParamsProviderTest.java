package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification.PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider;

@ExtendWith(MockitoExtension.class)
class PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitTransferAcceptedCommonDocumentTemplateWorkflowParamsProvider provider;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;
    

    @Test
    void constructParams() {

        final String receiverRequestId = "receiverRequestId";
        final String transfererRequestId = "transfererRequestId";
        final PermitTransferARequestPayload transfererPayload = PermitTransferARequestPayload.builder().relatedRequestId(receiverRequestId).build();
        final LocalDate transferDate = LocalDate.of(2022, 1, 1);
        final PermitTransferBRequestPayload receiverPayload = PermitTransferBRequestPayload.builder()
            .relatedRequestId(transfererRequestId)
            .determination(PermitIssuanceGrantDetermination.builder().activationDate(transferDate).build())
            .build();
        final long transfererAccountId = 1L;
        final long receiverAccountId = 2L;
        final String transfererPermitId = "transfererPermitId";
        final String transfererInstallationName = "transfererInstallationName";
        final String transferer = "transferer";
        final String receiver = "receiver";
        final String receiverPermitId = "receiverPermitId";
        final AddressDTO transfererAddress = AddressDTO.builder().line1("line1").build();
        final String transfererAddressFormatted = "transfererAddressFormatted";

        final Request receiverRequest = Request.builder()
            .payload(receiverPayload)
            .accountId(receiverAccountId).build();

        final Request transfererRequest = Request.builder()
            .payload(transfererPayload)
            .accountId(transfererAccountId).build();
        
        when(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(transfererAccountId)).thenReturn(
            InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .name(transfererInstallationName)
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                    .name(transferer)
                    .address(transfererAddress)
                    .build())
                .build());
        when(permitQueryService.getPermitIdByAccountId(transfererAccountId)).thenReturn(Optional.of(transfererPermitId));
        when(documentTemplateLocationInfoResolver.constructAddressInfo(transfererAddress)).thenReturn(transfererAddressFormatted);
        when(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(receiverAccountId)).thenReturn(
            InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                    .name(receiver)
                    .build())
                .build());
        when(permitQueryService.getPermitIdByAccountId(receiverAccountId)).thenReturn(Optional.of(receiverPermitId));
        
        final Map<String, Object> result = provider.constructParams(receiverRequest, transfererRequest);

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
            "transfererPermitId", transfererPermitId,
            "transferer", transferer,
            "transfererInstallationName", transfererInstallationName,
            "transfererInstallationAddress", transfererAddressFormatted,
            "receiver", receiver,
            "receiverPermitId", receiverPermitId,
            "transferDate", Date.from(transferDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        ));
    }
}
