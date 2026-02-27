package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationReportableEmissionsAddRequestActionServiceTest {

    @InjectMocks
    private InstallationReportableEmissionsAddRequestActionService installationReportableEmissionsAddRequestActionService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationAccountQueryOrchestrator installationAccountQueryOrchestrator;

    @Mock
    private PermitQueryService permitQueryService;

    @Test
    void shouldAddRequestAction() {
        Long accountId = 1L;
        String requestId = "requestId";
        Request request = buildRequest();

        InstallationReportableEmissionsRequestActionDTO installationReportableEmissionsRequestActionDTO = buildInstallationReportableEmissionsRequestActionDTO(LegalEntityType.SOLE_TRADER);
        PermitContainer permitContainer = buildPermitContainer(LegalEntityType.SOLE_TRADER);
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO(LegalEntityType.SOLE_TRADER);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryOrchestrator.getAccountWithPermit(accountId)).thenReturn(installationAccountPermitDTO);

        installationReportableEmissionsAddRequestActionService.addRequestAction(requestId, installationReportableEmissionsRequestActionDTO, accountId);

        ArgumentCaptor<InstallationReportableEmissionsRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(InstallationReportableEmissionsRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.INSTALLATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY)
        );

        InstallationReportableEmissionsRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        verify(requestService).findRequestById(requestId);
        verify(permitQueryService).getPermitContainerByAccountId(accountId);
    }

    @Test
    void shouldAddRequestAction_other() {
        Long accountId = 1L;
        String requestId = "requestId";
        Request request = buildRequest();

        InstallationReportableEmissionsRequestActionDTO installationReportableEmissionsRequestActionDTO = buildInstallationReportableEmissionsRequestActionDTO(LegalEntityType.SOLE_TRADER);
        PermitContainer permitContainer = buildPermitContainer(LegalEntityType.OTHER);
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO(LegalEntityType.OTHER);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryOrchestrator.getAccountWithPermit(accountId)).thenReturn(installationAccountPermitDTO);

        installationReportableEmissionsAddRequestActionService.addRequestAction(requestId, installationReportableEmissionsRequestActionDTO, accountId);

        ArgumentCaptor<InstallationReportableEmissionsRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(InstallationReportableEmissionsRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.INSTALLATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY)
        );

        InstallationReportableEmissionsRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        verify(requestService).findRequestById(requestId);
        verify(permitQueryService).getPermitContainerByAccountId(accountId);
    }

    private Request buildRequest() {
        return Request.builder()
                .id("requestId")
                .build();
    }

    private InstallationReportableEmissionsRequestActionDTO buildInstallationReportableEmissionsRequestActionDTO(LegalEntityType legalEntityType) {
        return InstallationReportableEmissionsRequestActionDTO.builder()
                .installationName("accountName")
                .emitterId("emitterId")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .legalEntityDTO(LegalEntityDTO.builder()
                        .name("entityName")
                        .type(legalEntityType)
                        .referenceNumber("entityRef123")
                        .address(AddressDTO.builder()
                                .line1("address1")
                                .city("city1")
                                .country("GR")
                                .postcode("12345")
                                .build())
                        .build())
                .reportableEmissions("2000")
                .reportingYear(Year.now().minusYears(1))
                .build();
    }

    private InstallationAccountPermitDTO buildInstallationAccountPermitDTO(LegalEntityType legalEntityType) {
        InstallationAccountDTO account = InstallationAccountDTO.builder()
                .id(1L)
                .accountType(AccountType.INSTALLATION)
                .name("accountName")
                .emitterId("emitterId")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.now())
                .registryReportingFirstYear(2025)
                .legalEntity(LegalEntityDTO.builder()
                        .name("entityName")
                        .type(legalEntityType)
                        .referenceNumber("entityRef123")
                        .address(AddressDTO.builder()
                                .line1("address1")
                                .city("city1")
                                .country("GR")
                                .postcode("12345")
                                .build())
                        .build())
                .location(LocationDTO.builder().type(LocationType.ONSHORE).build())
                .acceptedDate(LocalDateTime.now())
                .status(InstallationAccountStatus.LIVE)
                .build();

        PermitDetailsDTO detailsDTO = PermitDetailsDTO.builder()
                .id("permitId")
                .activationDate(LocalDate.now())
                .regulatedActivities(RegulatedActivities.builder()
                        .regulatedActivities(List.of(
                                RegulatedActivity.builder()
                                        .type(RegulatedActivityType.COMBUSTION)
                                        .build()))
                        .build())
                .build();

        return InstallationAccountPermitDTO.builder()
                .account(account)
                .permit(detailsDTO)
                .build();
    }

    private PermitContainer buildPermitContainer(LegalEntityType operatorType) {
        InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder()
                .installationName("Installation Name")
                .operator("Operator Name")
                .operatorType(operatorType)
                .companyReferenceNumber("companyReferenceNumber")
                .operatorDetailsAddress(AddressDTO.builder()
                        .line1("address1")
                        .city("city1")
                        .country("GR")
                        .postcode("12345")
                        .build())
                .build();

        return PermitContainer.builder()
                .permit(Permit.builder().build())
                .installationOperatorDetails(operatorDetails)
                .build();
    }
}