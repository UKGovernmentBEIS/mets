package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountCreatedRequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuancePartnershipDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceRegistryIntegrationRequestActionPayload;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceRegistryIntegrationAddRequestActionServiceTest {

    @InjectMocks
    private EmpIssuanceRegistryIntegrationAddRequestActionService empIssuanceRegistryIntegrationAddRequestActionService;

    @Mock
    private RequestService requestService;

    @Test
    void addRequestAction_buildsPayload_forLimitedCompany() {
        String requestId = "REQ-1";
        Request request = Request.builder().id(requestId).build();

        LimitedCompanyOrganisation organisation = LimitedCompanyOrganisation.builder()
                .organisationLocation(LocationOnShoreStateDTO.builder().build())
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .build(); // getLegalStatusType() should return LIMITED_COMPANY based on type

        AviationAccountCreatedRequestActionDTO dto = AviationAccountCreatedRequestActionDTO.builder()
                .emitterId("EMITTER-1")
                .permitId("PERMIT-1")
                .operatorName("Operator-1")
                .firstKnownAviationActivity(LocalDate.of(2025, 1, 1))
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .organisationStructure(organisation)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        ArgumentCaptor<EmpIssuanceRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(EmpIssuanceRegistryIntegrationRequestActionPayload.class);

        empIssuanceRegistryIntegrationAddRequestActionService.addRequestAction(requestId, dto);

        // then
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addSystemActionToRequest(eq(request), payloadCaptor.capture(),
                        eq(RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY));

        EmpIssuanceRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        EmpIssuanceOperatorDetails operatorDetails = payload.getOperatorDetails();
        assertEquals("EMITTER-1", operatorDetails.getEmitterId());
        assertEquals("PERMIT-1", operatorDetails.getEmissionsPlanId());
        assertEquals("Operator-1", operatorDetails.getOperatorName());
        assertEquals(LocalDate.of(2025,1,1), operatorDetails.getFirstKnownAviationActivity());
        assertEquals(CompetentAuthorityEnum.ENGLAND, operatorDetails.getRegulator());

        EmpIssuanceOrganisationDetails organisationDetails = payload.getOrganisationDetails();
        assertInstanceOf(EmpIssuanceLimitedCompanyDetails.class, organisationDetails);
        EmpIssuanceLimitedCompanyDetails limitedCompanyDetails = (EmpIssuanceLimitedCompanyDetails) organisationDetails;
        assertEquals(OrganisationLegalStatusType.LIMITED_COMPANY, limitedCompanyDetails.getOrganisationLegalStatus());
        assertNotNull(limitedCompanyDetails.getRegisteredAddress());
    }

    @Test
    void addRequestAction_buildsPayload_forIndividual() {
        String requestId = "REQ-1";
        Request request = Request.builder().id(requestId).build();

        IndividualOrganisation organisation = IndividualOrganisation.builder()
                .organisationLocation(LocationOnShoreStateDTO.builder().build())
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .fullName("Full Name")
                .build();

        AviationAccountCreatedRequestActionDTO dto = AviationAccountCreatedRequestActionDTO.builder()
                .emitterId("EMITTER-1")
                .permitId("PERMIT-1")
                .operatorName("Operator-1")
                .firstKnownAviationActivity(LocalDate.of(2025, 1, 1))
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .organisationStructure(organisation)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        ArgumentCaptor<EmpIssuanceRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(EmpIssuanceRegistryIntegrationRequestActionPayload.class);

        empIssuanceRegistryIntegrationAddRequestActionService.addRequestAction(requestId, dto);

        // then
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addSystemActionToRequest(eq(request), payloadCaptor.capture(),
                        eq(RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY));

        EmpIssuanceRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        EmpIssuanceOperatorDetails operatorDetails = payload.getOperatorDetails();
        assertEquals("EMITTER-1", operatorDetails.getEmitterId());
        assertEquals("PERMIT-1", operatorDetails.getEmissionsPlanId());
        assertEquals("Operator-1", operatorDetails.getOperatorName());
        assertEquals(LocalDate.of(2025,1,1), operatorDetails.getFirstKnownAviationActivity());
        assertEquals(CompetentAuthorityEnum.ENGLAND, operatorDetails.getRegulator());

        EmpIssuanceOrganisationDetails organisationDetails = payload.getOrganisationDetails();
        assertInstanceOf(EmpIssuanceIndividualCompanyDetails.class, organisationDetails);
        EmpIssuanceIndividualCompanyDetails limitedCompanyDetails = (EmpIssuanceIndividualCompanyDetails) organisationDetails;
        assertEquals(OrganisationLegalStatusType.INDIVIDUAL, limitedCompanyDetails.getOrganisationLegalStatus());
        assertNotNull(limitedCompanyDetails.getFullName());
    }

    @Test
    void addRequestAction_buildsPayload_forPartnership() {
        String requestId = "REQ-1";
        Request request = Request.builder().id(requestId).build();

        PartnershipOrganisation organisation = PartnershipOrganisation.builder()
                .organisationLocation(LocationOnShoreStateDTO.builder().build())
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .partnershipName("name")
                .build();

        AviationAccountCreatedRequestActionDTO dto = AviationAccountCreatedRequestActionDTO.builder()
                .emitterId("EMITTER-1")
                .permitId("PERMIT-1")
                .operatorName("Operator-1")
                .firstKnownAviationActivity(LocalDate.of(2025, 1, 1))
                .competentAuthority(CompetentAuthorityEnum.SCOTLAND)
                .organisationStructure(organisation)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        ArgumentCaptor<EmpIssuanceRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(EmpIssuanceRegistryIntegrationRequestActionPayload.class);

        empIssuanceRegistryIntegrationAddRequestActionService.addRequestAction(requestId, dto);

        // then
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addSystemActionToRequest(eq(request), payloadCaptor.capture(),
                        eq(RequestActionType.EMP_ISSUANCE_UKETS_ACCOUNT_CREATED_SENT_TO_REGISTRY));

        EmpIssuanceRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);

        EmpIssuanceOperatorDetails operatorDetails = payload.getOperatorDetails();
        assertEquals("EMITTER-1", operatorDetails.getEmitterId());
        assertEquals("PERMIT-1", operatorDetails.getEmissionsPlanId());
        assertEquals("Operator-1", operatorDetails.getOperatorName());
        assertEquals(LocalDate.of(2025,1,1), operatorDetails.getFirstKnownAviationActivity());
        assertEquals(CompetentAuthorityEnum.SCOTLAND, operatorDetails.getRegulator());

        EmpIssuanceOrganisationDetails organisationDetails = payload.getOrganisationDetails();
        assertInstanceOf(EmpIssuancePartnershipDetails.class, organisationDetails);
        EmpIssuancePartnershipDetails limitedCompanyDetails = (EmpIssuancePartnershipDetails) organisationDetails;
        assertEquals(OrganisationLegalStatusType.PARTNERSHIP, limitedCompanyDetails.getOrganisationLegalStatus());
        assertNotNull(limitedCompanyDetails.getPartnershipName());
    }
}