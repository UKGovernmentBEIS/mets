package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountAmendService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskValidationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningAmendApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningAmendApplicationActionHandlerTest {

    @InjectMocks
    private InstallationAccountOpeningAmendApplicationActionHandler handler;

	@Mock
	private RequestTaskService requestTaskService;

    @Mock
    private InstallationAccountAmendService installationAccountAmendService;

    @Mock
    private RequestTaskValidationService requestTaskValidationService;

    @Test
    void doProcess() {
    	//prepare data
    	final Long accountId = 1L;
    	PmrvUser userSubmitted = PmrvUser.builder().userId("user1").build();
    	RequestTask requestTask = createRequestTask(userSubmitted, accountId);
        InstallationAccountPayload initialAccountPayload = createAccountPayload("accountname", "leName");
        InstallationAccountOpeningApplicationRequestTaskPayload requestTaskPayload = InstallationAccountOpeningApplicationRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
            .accountPayload(initialAccountPayload)
            .build();
        requestTask.setPayload(requestTaskPayload);
		InstallationAccountDTO initialAccountDTO = createAccountDTO("accountname", "leName");

		InstallationAccountPayload amendAccountPayload = createAccountPayload("accountname", "leName2");
        InstallationAccountOpeningAmendApplicationRequestTaskActionPayload amendAccountSubmitPayload = InstallationAccountOpeningAmendApplicationRequestTaskActionPayload
            .builder()
            .payloadType(RequestTaskActionPayloadType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION_PAYLOAD)
            .accountPayload(amendAccountPayload)
            .build();

        PmrvUser userAmend = PmrvUser.builder().userId("user2").build();
		InstallationAccountDTO amendAccountDTO = createAccountDTO("accountname", "leName2");

        InstallationAccountOpeningApplicationRequestTaskPayload newRequestTaskPayload = InstallationAccountOpeningApplicationRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
            .accountPayload(amendAccountPayload)
            .build();

		when(requestTaskService.findTaskById(10L)).thenReturn(requestTask);
		doNothing().when(requestTaskValidationService).validateRequestTaskPayload(newRequestTaskPayload);

		//invoke
    	handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION, userAmend, amendAccountSubmitPayload);

    	//verify
    	verify(installationAccountAmendService, times(1)).amendAccount(accountId, initialAccountDTO, amendAccountDTO, userAmend);
        verify(requestTaskService, times(1)).updateRequestTaskPayload(requestTask, newRequestTaskPayload);
        verify(requestTaskValidationService, times(1)).validateRequestTaskPayload(newRequestTaskPayload);
    }

    private RequestTask createRequestTask(PmrvUser user, Long accountId) {
    	return RequestTask.builder()
				.id(10L)
				.request(Request.builder()
						.type(RequestType.INSTALLATION_ACCOUNT_OPENING)
						.competentAuthority(CompetentAuthorityEnum.WALES)
						.status(RequestStatus.IN_PROGRESS)
						.accountId(accountId)
						.requestActions(new ArrayList<>())
						.build())
				.build();
    }

    private InstallationAccountPayload createAccountPayload(String accountName, String leName) {
        return InstallationAccountPayload.builder()
	            .accountType(AccountType.INSTALLATION)
	            .name(accountName)
	            .competentAuthority(CompetentAuthorityEnum.WALES)
	            .commencementDate(LocalDate.of(2020,8,6))
	            .location(LocationOnShoreDTO.builder().build())
	            .legalEntity(LegalEntityDTO.builder()
	                .type(LegalEntityType.PARTNERSHIP)
	                .name(leName)
	                .referenceNumber("09546038")
	                .noReferenceNumberReason("noCompaniesRefDetails")
	                .address(AddressDTO.builder().build())
	                .build())
	            .build();
    }

    private InstallationAccountDTO createAccountDTO(String accountName, String leName) {
        return InstallationAccountDTO.builder()
        		.accountType(AccountType.INSTALLATION)
                .name(accountName)
                .competentAuthority(CompetentAuthorityEnum.WALES)
                .commencementDate(LocalDate.of(2020,8,6))
                .location(LocationOnShoreDTO.builder().build())
                .legalEntity(LegalEntityDTO.builder()
                		.type(LegalEntityType.PARTNERSHIP)
                        .name(leName)
                        .referenceNumber("09546038")
                        .noReferenceNumberReason("noCompaniesRefDetails")
                        .address(AddressDTO.builder().build())
                        .build())
                .build();
    }
}
