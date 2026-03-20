package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request.requestaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationReportableEmissionsAddRequestActionServiceTest {

    @InjectMocks
    private AviationReportableEmissionsAddRequestActionService service;

    @Mock
    private RequestService requestService;


    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Test
    void addRequestAction_requestFound_addsRequestAction() {

        Long accountId = 1L;
        String requestId = "AVI-0001-2023";
        Year year = Year.of(2023);
        Integer registryId = 123;
        BigDecimal reportableEmissions = BigDecimal.valueOf(2000);

        Request request = Request.builder().id(requestId).build();

        AviationAccountDTO accountDTO = AviationAccountDTO.builder()
                .id(accountId)
                .accountType(AccountType.AVIATION)
                .name("Test Aviation Account")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.of(2023, 1, 1))
                .registryId(registryId)
                .build();

        AviationReportableEmissionsUpdatedEvent event = AviationReportableEmissionsUpdatedEvent.builder()
                .accountId(accountId)
                .reportableEmissions(reportableEmissions)
                .year(year)
                .isFromDre(false)
                .isFromRegulator(false)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(accountDTO);

        service.addRequestAction(requestId, accountId, event);

        ArgumentCaptor<AviationReportableEmissionsRegistryIntegrationRequestActionPayload> payloadCaptor =
                ArgumentCaptor.forClass(AviationReportableEmissionsRegistryIntegrationRequestActionPayload.class);

        verify(requestService).findRequestById(requestId);
        verify(aviationAccountQueryService).getAviationAccountDTOById(accountId);
        verify(requestService).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.eq(request),
                payloadCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(RequestActionType.AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY)
        );

        AviationReportableEmissionsRegistryIntegrationRequestActionPayload payload = payloadCaptor.getValue();
        assertNotNull(payload);
        assertEquals(RequestActionPayloadType.AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY_PAYLOAD, payload.getPayloadType());
        assertEquals(registryId, payload.getRegistryId());
        assertEquals(String.valueOf(reportableEmissions), payload.getReportableEmissions());
        assertEquals(year, payload.getReportingYear());
    }

    @Test
    void addRequestAction_requestNotFound_returnsEarly() {

        Long accountId = 1L;
        String requestId = "AVI-0001-2023";
        Year year = Year.of(2023);

        AviationReportableEmissionsUpdatedEvent event = AviationReportableEmissionsUpdatedEvent.builder()
                .accountId(accountId)
                .reportableEmissions(BigDecimal.valueOf(2000))
                .year(year)
                .isFromDre(false)
                .isFromRegulator(false)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(null);

        service.addRequestAction(requestId, accountId, event);

        verify(requestService).findRequestById(requestId);
        verifyNoInteractions(aviationAccountQueryService);
        verify(requestService, never()).addSystemActionToRequest(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
    }
}

