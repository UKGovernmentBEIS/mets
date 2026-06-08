package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERInitiationType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NerCreateActionHandlerTest {

	@InjectMocks
	private NerCreateActionHandler cut;

	@Mock
	private StartProcessRequestService startProcessRequestService;

	@Test
	void process() {

		final Long accountId = 1L;
		final AppUser appUser = AppUser.builder().userId("user").build();
		final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();

		when(startProcessRequestService.startProcess(any(RequestParams.class)))
				.thenReturn(Request.builder().id("reqId").build());

		final String result = cut.process(accountId, payload, appUser);

		assertThat(result).isEqualTo("reqId");

		ArgumentCaptor<RequestParams> requestParamsCaptor =
				ArgumentCaptor.forClass(RequestParams.class);

		verify(startProcessRequestService).startProcess(requestParamsCaptor.capture());

		RequestParams requestParams = requestParamsCaptor.getValue();

		assertThat(requestParams.getAccountId()).isEqualTo(accountId);
		assertThat(requestParams.getType()).isEqualTo(RequestType.NER);

		NerRequestPayload requestPayload =
				(NerRequestPayload) requestParams.getRequestPayload();

		assertThat(requestPayload.getPayloadType())
				.isEqualTo(RequestPayloadType.NER_REQUEST_PAYLOAD);
		assertThat(requestPayload.getOperatorAssignee())
				.isEqualTo(appUser.getUserId());
		assertThat(requestPayload.getNer()).isNotNull();

		NERRequestMetadata requestMetadata =
				(NERRequestMetadata) requestParams.getRequestMetadata();

		assertThat(requestMetadata.getType())
				.isEqualTo(RequestMetadataType.NER);
		assertThat(requestMetadata.getNerInitiationType())
				.isEqualTo(NERInitiationType.INITIATED);

		assertThat(requestParams.getProcessVars())
				.containsEntry(
						BpmnProcessConstants.NER_INITIATION_TYPE,
						NERInitiationType.INITIATED);
	}

	@Test
	void getRequestCreateActionType() {
		assertThat(cut.getRequestCreateActionType())
				.isEqualTo(RequestCreateActionType.NER);
	}
}
