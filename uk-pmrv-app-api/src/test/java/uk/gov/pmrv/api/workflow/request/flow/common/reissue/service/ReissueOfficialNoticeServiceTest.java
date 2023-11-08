package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

@ExtendWith(MockitoExtension.class)
class ReissueOfficialNoticeServiceTest {

	@InjectMocks
	private ReissueOfficialNoticeService cut;
	
	@Mock
	private RequestAccountContactQueryService requestAccountContactQueryService;
	
	@Mock
	private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
	
	@Mock
	private DocumentFileGeneratorService documentFileGeneratorService;
	
	@Mock
	private OfficialNoticeSendService officialNoticeSendService;
	
	@Mock
	private AccountQueryService accountQueryService;
	
	@Test
	void generateOfficialNotice() throws InterruptedException, ExecutionException {
		Long accountId = 1L;
		ReissueRequestMetadata requestMetadata = ReissueRequestMetadata.builder()
				.signatory("signatory")
				.build();
		Request request = Request.builder().metadata(requestMetadata).accountId(accountId).build();
		UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
				.email("email")
				.build();
		
		TemplateParams templateParams = TemplateParams.builder()
				.competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
						.competentAuthority(CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build())
						.build())
				.build();
		
		DocumentTemplateParamsSourceData paramsSourceData = DocumentTemplateParamsSourceData.builder()
				.contextActionType(DocumentTemplateGenerationContextActionType.REISSUE)
				.request(request)
				.signatory(requestMetadata.getSignatory())
				.accountPrimaryContact(accountPrimaryContact)
				.toRecipientEmail(accountPrimaryContact.getEmail())
				.build();
		
		FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
				.name("offnotice")
				.build();
		
		when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
				.thenReturn(Optional.of(accountPrimaryContact));
		
		when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(paramsSourceData))
				.thenReturn(templateParams);
		
		when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);
		when(documentFileGeneratorService.generateFileDocumentAsync(DocumentTemplateType.EMP_REISSUE_UKETS, templateParams, "Batch_variation_notice_UK_ETS.pdf")).thenReturn(CompletableFuture.completedFuture(fileInfoDTO));
		
		CompletableFuture<FileInfoDTO> resultFuture = cut.generateOfficialNotice(request);
		FileInfoDTO result = resultFuture.get();
		
		assertThat(result).isEqualTo(fileInfoDTO);
		
		verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(request);

		verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(paramsSourceData);

		verify(accountQueryService, times(1)).getAccountEmissionTradingScheme(accountId);
		verify(documentFileGeneratorService, times(1)).generateFileDocumentAsync(DocumentTemplateType.EMP_REISSUE_UKETS, templateParams, "Batch_variation_notice_UK_ETS.pdf");
	}
	
	@Test
	void sendOfficialNotice() {
		FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("off").uuid(UUID.randomUUID().toString())
    			.build();
    	FileInfoDTO permitDocument = FileInfoDTO.builder()
    			.name("permitDocument").uuid(UUID.randomUUID().toString())
    			.build();
    	ReissueRequestPayload requestPayload = ReissueRequestPayload.builder()
    			.payloadType(RequestPayloadType.REISSUE_REQUEST_PAYLOAD)
    			.officialNotice(officialNotice)
    			.document(permitDocument)
    			.build();
    	
		Request request = Request.builder()
				.payload(requestPayload)
				.build();
		
		cut.sendOfficialNotice(request);
		
		verify(officialNoticeSendService, times(1)).sendOfficialNotice(List.of(officialNotice, permitDocument), request);
	}
	
}
