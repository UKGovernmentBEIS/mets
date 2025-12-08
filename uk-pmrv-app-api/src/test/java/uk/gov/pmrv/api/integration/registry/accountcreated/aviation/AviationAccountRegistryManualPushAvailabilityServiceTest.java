package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryManualPushAvailabilityService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountRegistryManualPushAvailabilityServiceTest {

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private AviationAccountRegistryManualPushAvailabilityService availabilityService;

    @Test
    void isManualPushAvailable_returnsTrue_whenAllConditionsMet() {
        String requestId = "REQ-1";
        long accountId = 100L;

        ReflectionTestUtils.setField(availabilityService, "isAccountCreateEnabled", true);

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        AviationAccountDTO account = AviationAccountDTO.builder()
                .id(accountId)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .registryId(null)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(account);

        Boolean result = availabilityService.isManualPushAvailable(requestId);

        assertTrue(result);
    }

    @Test
    void isManualPushAvailable_returnsFalse_whenTradingSchemeNotAviation() {
        String requestId = "REQ-2";
        long accountId = 200L;

        ReflectionTestUtils.setField(availabilityService, "isAccountCreateEnabled", true);


        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        AviationAccountDTO account = AviationAccountDTO.builder()
                .id(accountId)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS) // not aviation
                .registryId(null)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(account);

        Boolean result = availabilityService.isManualPushAvailable(requestId);

        assertFalse(result);
    }

    @Test
    void isManualPushAvailable_returnsFalse_whenRegistryIdNotNull() {

        ReflectionTestUtils.setField(availabilityService, "isAccountCreateEnabled", true);
        String requestId = "REQ-3";
        long accountId = 300L;

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        AviationAccountDTO account = AviationAccountDTO.builder()
                .id(accountId)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .registryId(123456) // not null
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(account);

        Boolean result = availabilityService.isManualPushAvailable(requestId);

        assertFalse(result);
    }

    @Test
    void isManualPushAvailable_returnsFalse_whenRequestStatusNotInProgress() {
        ReflectionTestUtils.setField(availabilityService, "isAccountCreateEnabled", true);

        String requestId = "REQ-4";
        long accountId = 400L;

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .status(RequestStatus.COMPLETED) // not in progress
                .build();

        AviationAccountDTO account = AviationAccountDTO.builder()
                .id(accountId)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .registryId(null)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(aviationAccountQueryService.getAviationAccountDTOById(accountId)).thenReturn(account);

        Boolean result = availabilityService.isManualPushAvailable(requestId);

        assertFalse(result);
    }

    @Test
    void isManualPushAvailable_returnsFalse_whenAccountCreationNotEnabled() {
        ReflectionTestUtils.setField(availabilityService, "isAccountCreateEnabled", false);


        Boolean result = availabilityService.isManualPushAvailable("test");

        assertFalse(result);
    }
}