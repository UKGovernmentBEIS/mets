package uk.gov.pmrv.api.integration.registry.accountupdated.installation;

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
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.BusinessOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.IndividualOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.InstallationAccountRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.RegistryIntegrationActivePermit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationAccountUpdatedAddRequestActionServiceTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private InstallationAccountUpdatedAddRequestActionService installationAccountUpdatedAddRequestActionService;

    @Test
    void addRequestAction_sole_trader() {

        String requestId = "requestId";
        Request request = buildRequest();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO(LegalEntityType.SOLE_TRADER);
        PermitContainer permitContainer = buildPermitContainer(LegalEntityType.SOLE_TRADER, "companyRef123", null);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        installationAccountUpdatedAddRequestActionService.addRequestAction(requestId, installationAccountPermitDTO, permitContainer);

        verify(requestService).findRequestById(requestId);

        ArgumentCaptor<InstallationAccountRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(InstallationAccountRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.PERMIT_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        InstallationAccountRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);
        assertEquals(RequestActionPayloadType.PERMIT_VARIATION_REGISTRY_INTEGRATION_ACCOUNT_UPDATED_PAYLOAD, payload.getPayloadType());

        RegistryIntegrationActivePermit activePermit = payload.getActivePermit();
        assertNotNull(activePermit);
        assertEquals("emitterId", activePermit.getEmitterId());
        assertEquals("permitId", activePermit.getPermitId());
        assertEquals("Installation Name", activePermit.getInstallationName());
        assertEquals("Operator Name", activePermit.getOperatorName());
        assertNotNull(activePermit.getRegulatedActivitiesStartDate());
        assertEquals("EA", activePermit.getRegulator());

        IndividualOrganisationDetails organizationDetails = (IndividualOrganisationDetails) payload.getOrganizationDetails();
        assertNotNull(organizationDetails);
        assertEquals(LegalEntityType.SOLE_TRADER, organizationDetails.getOrganisationLegalStatus());
        assertNotNull(organizationDetails.getOperatorAddress());
        assertEquals("address1", organizationDetails.getOperatorAddress().getLine1());
    }

    @Test
    void addRequestAction_limited_company_with_reference_number() {

        String requestId = "requestId";
        Request request = buildRequest();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO(LegalEntityType.LIMITED_COMPANY);
        PermitContainer permitContainer = buildPermitContainer(LegalEntityType.LIMITED_COMPANY, "companyRef123", null);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        installationAccountUpdatedAddRequestActionService.addRequestAction(requestId, installationAccountPermitDTO, permitContainer);

        verify(requestService).findRequestById(requestId);

        ArgumentCaptor<InstallationAccountRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(InstallationAccountRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.PERMIT_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        InstallationAccountRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        BusinessOrganisationDetails organizationDetails = (BusinessOrganisationDetails) payload.getOrganizationDetails();
        assertNotNull(organizationDetails);
        assertEquals(LegalEntityType.LIMITED_COMPANY, organizationDetails.getOrganisationLegalStatus());
        assertEquals("companyRef123", organizationDetails.getCompanyRegistrationNumber());
        assertNull(organizationDetails.getJustification());
        assertNotNull(organizationDetails.getRegisteredAddress());
        assertEquals("address1", organizationDetails.getRegisteredAddress().getLine1());
    }

    @Test
    void addRequestAction_limited_company_without_reference_number() {

        String requestId = "requestId";
        Request request = buildRequest();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO(LegalEntityType.LIMITED_COMPANY);
        installationAccountPermitDTO.getAccount().getLegalEntity().setNoReferenceNumberReason("No reference number reason");
        PermitContainer permitContainer = buildPermitContainer(LegalEntityType.LIMITED_COMPANY, null, "No reference number reason");

        when(requestService.findRequestById(requestId)).thenReturn(request);

        installationAccountUpdatedAddRequestActionService.addRequestAction(requestId, installationAccountPermitDTO, permitContainer);

        ArgumentCaptor<InstallationAccountRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(InstallationAccountRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.PERMIT_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        InstallationAccountRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        BusinessOrganisationDetails organizationDetails = (BusinessOrganisationDetails) payload.getOrganizationDetails();
        assertNull(organizationDetails.getCompanyRegistrationNumber());
        assertEquals("No reference number reason", organizationDetails.getJustification());
    }

    @Test
    void addRequestAction_partnership() {

        String requestId = "requestId";
        Request request = buildRequest();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO(LegalEntityType.PARTNERSHIP);
        PermitContainer permitContainer = buildPermitContainer(LegalEntityType.PARTNERSHIP, "partnershipRef456", null);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        installationAccountUpdatedAddRequestActionService.addRequestAction(requestId, installationAccountPermitDTO, permitContainer);

        ArgumentCaptor<InstallationAccountRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(InstallationAccountRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.PERMIT_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        InstallationAccountRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        BusinessOrganisationDetails organizationDetails = (BusinessOrganisationDetails) payload.getOrganizationDetails();
        assertNotNull(organizationDetails);
        assertEquals(LegalEntityType.PARTNERSHIP, organizationDetails.getOrganisationLegalStatus());
        assertEquals("partnershipRef456", organizationDetails.getCompanyRegistrationNumber());
    }

    @Test
    void addRequestAction_other() {

        String requestId = "requestId";
        Request request = buildRequest();
        InstallationAccountPermitDTO installationAccountPermitDTO = buildInstallationAccountPermitDTO(LegalEntityType.OTHER);
        PermitContainer permitContainer = buildPermitContainer(LegalEntityType.OTHER, "otherRef789", null);

        when(requestService.findRequestById(requestId)).thenReturn(request);

        installationAccountUpdatedAddRequestActionService.addRequestAction(requestId, installationAccountPermitDTO, permitContainer);

        ArgumentCaptor<InstallationAccountRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(InstallationAccountRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.PERMIT_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        InstallationAccountRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        BusinessOrganisationDetails organizationDetails = (BusinessOrganisationDetails) payload.getOrganizationDetails();
        assertNotNull(organizationDetails);
        assertEquals(LegalEntityType.OTHER, organizationDetails.getOrganisationLegalStatus());
        assertEquals("otherRef789", organizationDetails.getCompanyRegistrationNumber());
    }

    private Request buildRequest() {
        return Request.builder()
                .id("requestId")
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

    private PermitContainer buildPermitContainer(LegalEntityType operatorType, String companyReferenceNumber, String noReferenceNumberReason) {
        InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder()
                .installationName("Installation Name")
                .operator("Operator Name")
                .operatorType(operatorType)
                .companyReferenceNumber(companyReferenceNumber)
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