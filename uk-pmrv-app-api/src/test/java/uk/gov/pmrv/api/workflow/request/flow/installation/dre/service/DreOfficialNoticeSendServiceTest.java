package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@ExtendWith(MockitoExtension.class)
class DreOfficialNoticeSendServiceTest {
	
	@InjectMocks
    private DreOfficialNoticeSendService cut;
	
	@Mock
	private RequestService requestService;
	
	@Mock 
	private DecisionNotificationUsersService decisionNotificationUsersService;
	
	@Mock
	private OfficialNoticeSendService officialNoticeSendService;

	@Test
	void sendOfficialNotice() {
		String requestId = "1";
		FileInfoDTO officialDocFileInfoDTO = FileInfoDTO.builder()
                .name("offDoc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();

		DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of("operator"))
				.signatory("signatory").build();
		DreRequestPayload requestPayload = DreRequestPayload.builder().decisionNotification(decisionNotification)
				.officialNotice(officialDocFileInfoDTO)
				.build();
		Request request = Request.builder().id(requestId).payload(requestPayload).build();
		List<String> decisionNotificationUserEmails = List.of("operator@pmrv.uk");
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		when(decisionNotificationUsersService.findUserEmails(decisionNotification))
    		.thenReturn(decisionNotificationUserEmails);
		
		cut.sendOfficialNotice(requestId);
		
		verify(requestService, times(1)).findRequestById(requestId);
		verify(decisionNotificationUsersService, times(1)).findUserEmails(decisionNotification);
		verify(officialNoticeSendService, times(1))
			.sendOfficialNotice(List.of(requestPayload.getOfficialNotice()), request, decisionNotificationUserEmails);
	}
}
