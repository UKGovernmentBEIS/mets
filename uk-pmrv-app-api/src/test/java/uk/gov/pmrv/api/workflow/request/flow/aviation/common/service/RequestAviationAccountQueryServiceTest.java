package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestAviationAccountQueryServiceTest {

    @InjectMocks
    private RequestAviationAccountQueryService requestAviationAccountQueryService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Test
    void getAccountInfo() {
        Long accountId = 1L;
        String accountName = "accountName";
        String crcoCode = "crcoCode";

        AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder()
            .name(accountName)
            .crcoCode(crcoCode)
            .build();

        ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
            .name("userName")
            .roleCode("operator")
            .email("email")
            .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(accountInfo);
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.of(serviceContactDetails));

        RequestAviationAccountInfo expected = RequestAviationAccountInfo.builder()
            .operatorName(accountName)
            .crcoCode(crcoCode)
            .serviceContactDetails(serviceContactDetails)
            .build();

        RequestAviationAccountInfo result = requestAviationAccountQueryService.getAccountInfo(accountId);

        assertEquals(expected, result);
    }

    @Test
    void getAccountInfo_throws_exception_when_no_service_contact() {
        Long accountId = 1L;

        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class,
            () -> requestAviationAccountQueryService.getAccountInfo(accountId));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());
        verifyNoInteractions(aviationAccountQueryService);
    }
}