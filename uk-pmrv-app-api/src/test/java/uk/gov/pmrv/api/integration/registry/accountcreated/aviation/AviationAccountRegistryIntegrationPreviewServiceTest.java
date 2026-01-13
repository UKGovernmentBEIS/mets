package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryIntegrationPreviewService;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryViewDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationLimitedCompanyDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountRegistryIntegrationPreviewServiceTest {

    @Mock
    private RequestService requestService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @InjectMocks
    private AviationAccountRegistryIntegrationPreviewService service;

    @Test
    void getAviationAccountRegistryView() {
        String requestId = "1";
        Long accountId = 100L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        String registrationNumber = "REG123";

        LocationOnShoreStateDTO location = LocationOnShoreStateDTO.builder()
                .line1("line1")
                .city("city")
                .country("GR")
                .build();

        LimitedCompanyOrganisation structure = LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber(registrationNumber)
                .organisationLocation(location)
                .build();

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .operatorDetails(EmpOperatorDetails.builder()
                                .operatorName("Operator Name")
                                .organisationStructure(structure)
                                .build())
                        .build())
                .build();

        RequestTask reviewTask = RequestTask.builder()
                .type(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW)
                .payload(payload)
                .build();

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .requestTasks(List.of(reviewTask))
                .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .emitterId("EM123")
                .commencementDate(commencementDate)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        AviationAccountRegistryViewDTO result = service.getAviationAccountRegistryView(requestId);

        assertNotNull(result);
        assertEquals("EM123", result.getOperatorDetails().getEmitterId());
        assertEquals("Operator Name", result.getOperatorDetails().getOperatorName());
        assertEquals("EA", result.getOperatorDetails().getRegulator());

        AviationLimitedCompanyDetails orgDetails = (AviationLimitedCompanyDetails) result.getOrganisationDetails();
        assertEquals(OrganisationLegalStatusType.LIMITED_COMPANY, orgDetails.getOrganisationLegalStatus());
        assertEquals(registrationNumber, orgDetails.getCompanyRegistrationNumber());
        assertEquals(location, orgDetails.getRegisteredAddress());

        verify(requestService).findRequestById(requestId);
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
    }

    @Test
    void getAviationAccountRegistryView_when_waiting_for_amends() {
        String requestId = "1";
        Long accountId = 100L;
        LocalDate commencementDate = LocalDate.of(2023, 1, 1);
        String registrationNumber = "REG123";

        LocationOnShoreStateDTO location = LocationOnShoreStateDTO.builder()
            .line1("line1")
            .city("city")
            .country("GR")
            .build();

        LimitedCompanyOrganisation structure = LimitedCompanyOrganisation.builder()
            .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
            .registrationNumber(registrationNumber)
            .organisationLocation(location)
            .build();

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload payload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(EmpOperatorDetails.builder()
                    .operatorName("Operator Name")
                    .organisationStructure(structure)
                    .build())
                .build())
            .build();

        RequestTask reviewTask = RequestTask.builder()
            .type(RequestTaskType.EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS)
            .payload(payload)
            .build();

        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .requestTasks(List.of(reviewTask))
            .build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
            .emitterId("EM123")
            .commencementDate(commencementDate)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        AviationAccountRegistryViewDTO result = service.getAviationAccountRegistryView(requestId);

        assertNotNull(result);
        assertEquals("EM123", result.getOperatorDetails().getEmitterId());
        assertEquals("Operator Name", result.getOperatorDetails().getOperatorName());
        assertEquals("EA", result.getOperatorDetails().getRegulator());

        AviationLimitedCompanyDetails orgDetails = (AviationLimitedCompanyDetails) result.getOrganisationDetails();
        assertEquals(OrganisationLegalStatusType.LIMITED_COMPANY, orgDetails.getOrganisationLegalStatus());
        assertEquals(registrationNumber, orgDetails.getCompanyRegistrationNumber());
        assertEquals(location, orgDetails.getRegisteredAddress());

        verify(requestService).findRequestById(requestId);
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
    }
}