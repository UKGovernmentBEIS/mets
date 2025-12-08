package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.IndividualOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.PartnershipOrganisation;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryIntegrationPreviewService;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryViewDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationIndividualCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOrganisationDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationPartnershipDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;

@ExtendWith(MockitoExtension.class)
class AviationAccountRegistryIntegrationPreviewServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @InjectMocks
    private AviationAccountRegistryIntegrationPreviewService integrationPreviewService;

    @Test
    void getAviationAccountRegistryView_limitedCompany_buildsCorrectDTO() {
        String requestId = "REQ-1";
        long accountId = 11L;

        AviationAccountDTO account = AviationAccountDTO.builder()
                .id(accountId)
                .name("1")
                .emitterId("1")
                .commencementDate(LocalDate.of(2025, 1, 1))
                .competentAuthority(ENGLAND)
                .build();

        LocationOnShoreStateDTO location = LocationOnShoreStateDTO.builder().build();

        LimitedCompanyOrganisation organisation = LimitedCompanyOrganisation.builder()
                .organisationLocation(location)
                .registrationNumber("CRN-123456")
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .build();

        EmpIssuanceUkEtsRequestPayload payload = mock(EmpIssuanceUkEtsRequestPayload.class, RETURNS_DEEP_STUBS);
        when(payload.getEmissionsMonitoringPlan().getOperatorDetails().getOrganisationStructure())
                .thenReturn(organisation);

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .payload(payload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(account);

        AviationAccountRegistryViewDTO result = integrationPreviewService.getAviationAccountRegistryView(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(aviationAccountQueryService, times(1)).getAviationAccountDTOById(accountId);

        assertNotNull(result);

        AviationOperatorDetails op = result.getOperatorDetails();
        assertEquals("1", op.getEmitterId());
        assertEquals("1", op.getOperatorName());
        assertEquals(LocalDate.of(2025, 1, 1), op.getFirstKnownAviationActivity());
        assertEquals("EA", op.getRegulator());

        AviationOrganisationDetails orgDetails = result.getOrganisationDetails();
        assertInstanceOf(AviationLimitedCompanyDetails.class, orgDetails);
        AviationLimitedCompanyDetails ltd = (AviationLimitedCompanyDetails) orgDetails;
        assertEquals(OrganisationLegalStatusType.LIMITED_COMPANY, ltd.getOrganisationLegalStatus());
        assertEquals(location, ltd.getRegisteredAddress());
        assertEquals("CRN-123456", ltd.getCompanyRegistrationNumber());
    }

    @Test
    void getAviationAccountRegistryView_individual_buildsCorrectDTO() {
        String requestId = "REQ-1";
        long accountId = 11L;

        AviationAccountDTO account = AviationAccountDTO.builder()
                .id(accountId)
                .name("1")
                .emitterId("1")
                .commencementDate(LocalDate.of(2025, 1, 1))
                .competentAuthority(ENGLAND)
                .build();

        LocationOnShoreStateDTO location = LocationOnShoreStateDTO.builder().build();

        IndividualOrganisation organisation = IndividualOrganisation.builder()
                .organisationLocation(location)
                .fullName("company")
                .legalStatusType(OrganisationLegalStatusType.INDIVIDUAL)
                .build();

        EmpIssuanceUkEtsRequestPayload payload = mock(EmpIssuanceUkEtsRequestPayload.class, RETURNS_DEEP_STUBS);
        when(payload.getEmissionsMonitoringPlan().getOperatorDetails().getOrganisationStructure())
                .thenReturn(organisation);

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .payload(payload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(account);

        AviationAccountRegistryViewDTO result = integrationPreviewService.getAviationAccountRegistryView(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(aviationAccountQueryService, times(1)).getAviationAccountDTOById(accountId);

        assertNotNull(result);

        AviationOperatorDetails op = result.getOperatorDetails();
        assertEquals("1", op.getEmitterId());
        assertEquals("1", op.getOperatorName());
        assertEquals(LocalDate.of(2025, 1, 1), op.getFirstKnownAviationActivity());
        assertEquals("EA", op.getRegulator());

        AviationOrganisationDetails orgDetails = result.getOrganisationDetails();
        assertInstanceOf(AviationIndividualCompanyDetails.class, orgDetails);
        AviationIndividualCompanyDetails companyDetails = (AviationIndividualCompanyDetails) orgDetails;
        assertEquals(OrganisationLegalStatusType.INDIVIDUAL, companyDetails.getOrganisationLegalStatus());
        assertEquals("company", companyDetails.getFullName());
    }

    @Test
    void getAviationAccountRegistryView_partnership_buildsCorrectDTO() {
        String requestId = "REQ-1";
        long accountId = 11L;

        AviationAccountDTO account = AviationAccountDTO.builder()
                .id(accountId)
                .name("1")
                .emitterId("1")
                .commencementDate(LocalDate.of(2025, 1, 1))
                .competentAuthority(ENGLAND)
                .build();

        LocationOnShoreStateDTO location = LocationOnShoreStateDTO.builder().build();

        PartnershipOrganisation organisation = PartnershipOrganisation.builder()
                .organisationLocation(location)
                .partnershipName("partnership")
                .legalStatusType(OrganisationLegalStatusType.PARTNERSHIP)
                .build();

        EmpIssuanceUkEtsRequestPayload payload = mock(EmpIssuanceUkEtsRequestPayload.class, RETURNS_DEEP_STUBS);
        when(payload.getEmissionsMonitoringPlan().getOperatorDetails().getOrganisationStructure())
                .thenReturn(organisation);

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .payload(payload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(account);

        AviationAccountRegistryViewDTO result = integrationPreviewService.getAviationAccountRegistryView(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(aviationAccountQueryService, times(1)).getAviationAccountDTOById(accountId);

        assertNotNull(result);

        AviationOperatorDetails op = result.getOperatorDetails();
        assertEquals("1", op.getEmitterId());
        assertEquals("1", op.getOperatorName());
        assertEquals(LocalDate.of(2025, 1, 1), op.getFirstKnownAviationActivity());
        assertEquals("EA", op.getRegulator());

        AviationOrganisationDetails orgDetails = result.getOrganisationDetails();
        assertInstanceOf(AviationPartnershipDetails.class, orgDetails);
        AviationPartnershipDetails companyDetails = (AviationPartnershipDetails) orgDetails;
        assertEquals(OrganisationLegalStatusType.PARTNERSHIP, companyDetails.getOrganisationLegalStatus());
        assertEquals("partnership", companyDetails.getPartnershipName());
    }
}