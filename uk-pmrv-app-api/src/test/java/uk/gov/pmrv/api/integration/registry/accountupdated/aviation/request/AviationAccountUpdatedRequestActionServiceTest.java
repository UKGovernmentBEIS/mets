package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction.AviationAccountUpdatedRequestActionService;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction.AviationUpdateOperatorDetails;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction.EmpVariationRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationPartnershipDetails;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAccountUpdatedRequestActionServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @InjectMocks
    private AviationAccountUpdatedRequestActionService aviationAccountUpdatedRequestActionService;

    @Test
    void addRequestAction_limited_company() {

        String requestId = "requestId";
        Request request = buildRequest();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEts emp = buildEmissionsMonitoringPlanUkEtsContainer(buildLimitedCompanyOrganisation());

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));

        aviationAccountUpdatedRequestActionService.addRequestAction(requestId, accountDTO, emp);

        verify(requestService).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService).getEmpIdByAccountId(1L);

        ArgumentCaptor<EmpVariationRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(EmpVariationRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.EMP_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        EmpVariationRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);
        assertEquals(RequestActionPayloadType.EMP_VARIATION_UKETS_REGISTRY_INTEGRATION_ACCOUNT_UPDATED_PAYLOAD, payload.getPayloadType());

        AviationUpdateOperatorDetails operatorDetails = payload.getOperatorDetails();
        assertNotNull(operatorDetails);
        assertEquals(123, operatorDetails.getRegistryId());
        assertEquals("empId", operatorDetails.getEmissionsPlanId());
        assertEquals("Account Name", operatorDetails.getOperatorName());
        assertEquals(2023, operatorDetails.getFirstYearOfReportingObligation());

        AviationLimitedCompanyDetails organisationDetails = (AviationLimitedCompanyDetails) payload.getOrganisationDetails();
        assertNotNull(organisationDetails);
        assertEquals(OrganisationLegalStatusType.LIMITED_COMPANY, organisationDetails.getOrganisationLegalStatus());
        assertEquals("REG123456", organisationDetails.getCompanyRegistrationNumber());
        assertNotNull(organisationDetails.getRegisteredAddress());
    }

    @Test
    void addRequestAction_individual() {

        String requestId = "requestId";
        Request request = buildRequest();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEts emp = buildEmissionsMonitoringPlanUkEtsContainer(buildIndividualOrganisation());

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));

        aviationAccountUpdatedRequestActionService.addRequestAction(requestId, accountDTO, emp);

        verify(requestService).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService).getEmpIdByAccountId(1L);

        ArgumentCaptor<EmpVariationRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(EmpVariationRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.EMP_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        EmpVariationRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        AviationIndividualCompanyDetails organisationDetails = (AviationIndividualCompanyDetails) payload.getOrganisationDetails();
        assertNotNull(organisationDetails);
        assertEquals(OrganisationLegalStatusType.INDIVIDUAL, organisationDetails.getOrganisationLegalStatus());
        assertEquals("John Doe", organisationDetails.getFullName());
        assertNotNull(organisationDetails.getAddress());
    }

    @Test
    void addRequestAction_partnership() {

        String requestId = "requestId";
        Request request = buildRequest();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEts empContainer = buildEmissionsMonitoringPlanUkEtsContainer(buildPartnershipOrganisation());

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.of("empId"));

        aviationAccountUpdatedRequestActionService.addRequestAction(requestId, accountDTO, empContainer);

        verify(requestService).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService).getEmpIdByAccountId(1L);

        ArgumentCaptor<EmpVariationRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(EmpVariationRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.EMP_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        EmpVariationRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        AviationPartnershipDetails organisationDetails = (AviationPartnershipDetails) payload.getOrganisationDetails();
        assertNotNull(organisationDetails);
        assertEquals(OrganisationLegalStatusType.PARTNERSHIP, organisationDetails.getOrganisationLegalStatus());
        assertEquals("Partnership Name", organisationDetails.getPartnershipName());
        assertNotNull(organisationDetails.getMainOfficeAddress());
    }

    @Test
    void addRequestAction_emp_id_not_found() {

        String requestId = "requestId";
        Request request = buildRequest();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();
        EmissionsMonitoringPlanUkEts empContainer = buildEmissionsMonitoringPlanUkEtsContainer(buildLimitedCompanyOrganisation());

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(1L)).thenReturn(Optional.empty());

        aviationAccountUpdatedRequestActionService.addRequestAction(requestId, accountDTO, empContainer);

        verify(requestService).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService).getEmpIdByAccountId(1L);

        ArgumentCaptor<EmpVariationRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(EmpVariationRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.EMP_VARIATION_ACCOUNT_UPDATED_SENT_TO_REGISTRY)
        );

        EmpVariationRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);
        assertNotNull(payload.getOperatorDetails());
        assertEquals(null, payload.getOperatorDetails().getEmissionsPlanId());
    }

    private Request buildRequest() {
        return Request.builder()
                .id("requestId")
                .build();
    }

    private AviationAccountDTO buildAviationAccountDTO() {
        return AviationAccountDTO.builder()
                .id(1L)
                .accountType(AccountType.AVIATION)
                .name("Account Name")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.of(2023, 1, 1))
                .registryId(123)
                .build();
    }

    private EmissionsMonitoringPlanUkEts buildEmissionsMonitoringPlanUkEtsContainer(uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure organisationStructure) {
        EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder()
                .organisationStructure(organisationStructure)
                .build();

        return EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(operatorDetails)
                .build();

    }

    private LimitedCompanyOrganisation buildLimitedCompanyOrganisation() {
        return LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("REG123456")
                .organisationLocation(buildLocationOnShoreStateDTO())
                .build();
    }

    private IndividualOrganisation buildIndividualOrganisation() {
        return IndividualOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("John Doe")
                .organisationLocation(buildLocationOnShoreStateDTO())
                .build();
    }

    private PartnershipOrganisation buildPartnershipOrganisation() {
        return PartnershipOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("Partnership Name")
                .organisationLocation(buildLocationOnShoreStateDTO())
                .build();
    }

    private LocationOnShoreStateDTO buildLocationOnShoreStateDTO() {
        return LocationOnShoreStateDTO.builder()
                .line1("address1")
                .line2("address2")
                .city("city1")
                .state("state1")
                .postcode("12345")
                .country("GR")
                .build();
    }
}