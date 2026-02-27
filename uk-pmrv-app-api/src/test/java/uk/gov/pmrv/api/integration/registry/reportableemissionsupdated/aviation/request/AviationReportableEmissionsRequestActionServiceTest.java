package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.repository.EmissionsMonitoringPlanRepository;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.operatordetails.EmpOperatorDetails;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request.requestaction.AviationReportableEmissionsAddRequestActionService;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request.requestaction.AviationReportableEmissionsRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationReportableEmissionsRequestActionServiceTest {

    @InjectMocks
    private AviationReportableEmissionsAddRequestActionService aviationReportableEmissionsAddRequestActionService;

    @Mock
    private RequestService requestService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private EmissionsMonitoringPlanRepository emissionsMonitoringPlanRepository;

    @Test
    void addRequestAction() {
        Long accountId = 1L;
        String requestId = "requestId";
        Year year = Year.now();
        Request request = buildRequest();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();

        AviationReportableEmissionsUpdatedEvent reportableEmissionsUpdatedEvent = AviationReportableEmissionsUpdatedEvent.builder()
                .accountId(accountDTO.getId())
                .reportableEmissions(BigDecimal.valueOf(2000))
                .year(year)
                .isFromDre(true)
                .isFromRegulator(true)
                .build();

        LimitedCompanyOrganisation organisation = buildLimitedCompanyOrganisation();
        organisation.getOrganisationLocation().setCountry("GB");
        EmissionsMonitoringPlanUkEtsContainer empContainer = buildEmissionsMonitoringPlanUkEtsContainer(organisation);
        EmissionsMonitoringPlanUkEtsDTO empDto = EmissionsMonitoringPlanUkEtsDTO
                .builder()
                .id(UUID.randomUUID().toString())
                .empContainer(empContainer)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(empDto));
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        aviationReportableEmissionsAddRequestActionService.addRequestAction(requestId, accountId, reportableEmissionsUpdatedEvent);

        ArgumentCaptor<AviationReportableEmissionsRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(AviationReportableEmissionsRegistryIntegrationRequestActionPayload.class);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY)
        );

        verify(requestService).findRequestById(requestId);
        verify(emissionsMonitoringPlanQueryService).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);

        AviationReportableEmissionsRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);
        assertEquals(RequestActionPayloadType.AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY_PAYLOAD, payload.getPayloadType());
    }

    @Test
    void addRequestAction_emp_not_found() {
        Long accountId = 1L;
        String requestId = "requestId";
        Year year = Year.now();
        Request request = buildRequest();
        AviationAccountDTO accountDTO = buildAviationAccountDTO();

        AviationReportableEmissionsUpdatedEvent reportableEmissionsUpdatedEvent = AviationReportableEmissionsUpdatedEvent.builder()
                .accountId(accountDTO.getId())
                .reportableEmissions(BigDecimal.valueOf(2000))
                .year(year)
                .isFromDre(true)
                .isFromRegulator(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.empty());
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> aviationReportableEmissionsAddRequestActionService.addRequestAction(requestId, accountId, reportableEmissionsUpdatedEvent));
        Assertions.assertEquals(MetsErrorCode.NO_EMP_SERVICE_FOUND, ex.getErrorCode());

        verify(requestService).findRequestById(requestId);
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
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

    private LimitedCompanyOrganisation buildLimitedCompanyOrganisation() {
        return LimitedCompanyOrganisation.builder()
                .legalStatusType(OrganisationLegalStatusType.LIMITED_COMPANY)
                .registrationNumber("REG123456")
                .organisationLocation(buildLocationOnShoreStateDTO())
                .build();
    }

    private EmissionsMonitoringPlanUkEtsContainer buildEmissionsMonitoringPlanUkEtsContainer(uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure organisationStructure) {
        EmpOperatorDetails operatorDetails = EmpOperatorDetails.builder()
                .operatorName("emp container operator name")
                .organisationStructure(organisationStructure)
                .build();

        EmissionsMonitoringPlanUkEts emp = EmissionsMonitoringPlanUkEts.builder()
                .operatorDetails(operatorDetails)
                .build();

        return EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(emp)
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