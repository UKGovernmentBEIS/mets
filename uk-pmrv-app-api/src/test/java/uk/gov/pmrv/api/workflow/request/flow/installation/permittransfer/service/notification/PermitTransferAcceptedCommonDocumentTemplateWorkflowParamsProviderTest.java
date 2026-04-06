package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityWithoutHoldingCompanyDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.service.PermitIdentifierGenerator;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

    @Mock
    private PermitIdentifierGenerator generator;

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

        LocationDTO location = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).address(transfererAddress).build();
        
        when(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(transfererAccountId)).thenReturn(
            InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .name(transfererInstallationName)
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                    .name(transferer)
                    .address(transfererAddress)
                    .build())
                    .location(location)
                .build());
        when(permitQueryService.getPermitIdByAccountId(transfererAccountId)).thenReturn(Optional.of(transfererPermitId));
        when(documentTemplateLocationInfoResolver.constructLocationInfo(location)).thenReturn(transfererAddressFormatted);
        when(installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(receiverAccountId)).thenReturn(
            InstallationAccountWithoutLeHoldingCompanyDTO.builder()
                .legalEntity(LegalEntityWithoutHoldingCompanyDTO.builder()
                    .name(receiver)
                    .build())
                .build());
        when(permitQueryService.getPermitIdByAccountId(receiverAccountId)).thenReturn(Optional.of(receiverPermitId));
        when(generator.generate(receiverRequest.getAccountId())).thenReturn(receiverPermitId);
        
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
