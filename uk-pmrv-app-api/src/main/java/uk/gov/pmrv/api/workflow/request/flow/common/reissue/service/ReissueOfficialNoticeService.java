package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.EmissionTradingSchemeToReissueDocumentTemplateTypeMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper.EmissionTradingSchemeToOfficialNoticeFileNameMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

@Service
@RequiredArgsConstructor
public class ReissueOfficialNoticeService {
	
	private final RequestAccountContactQueryService requestAccountContactQueryService;
	private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
	private final DocumentFileGeneratorService documentFileGeneratorService;
	private final OfficialNoticeSendService officialNoticeSendService;
	private final AccountQueryService accountQueryService;
	
	private final EmissionTradingSchemeToReissueDocumentTemplateTypeMapper schemeToDocumentTemplateTypeMapper = Mappers
			.getMapper(EmissionTradingSchemeToReissueDocumentTemplateTypeMapper.class);
	private final EmissionTradingSchemeToOfficialNoticeFileNameMapper schemeToOfficialNoticeFileNameMapper = Mappers
			.getMapper(EmissionTradingSchemeToOfficialNoticeFileNameMapper.class);
	
	@Transactional
    public CompletableFuture<FileInfoDTO> generateOfficialNotice(final Request request) {
        final ReissueRequestMetadata requestMetadata = (ReissueRequestMetadata) request.getMetadata();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
			.orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
				.constructTemplateParams(DocumentTemplateParamsSourceData.builder()
						.contextActionType(DocumentTemplateGenerationContextActionType.REISSUE)
						.request(request)
						.signatory(requestMetadata.getSignatory())
						.accountPrimaryContact(accountPrimaryContact)
						.toRecipientEmail(accountPrimaryContact.getEmail())
						.build());
        
        final EmissionTradingScheme scheme = accountQueryService.getAccountEmissionTradingScheme(request.getAccountId());
		return documentFileGeneratorService.generateAndSaveFileDocumentAsync(schemeToDocumentTemplateTypeMapper.map(scheme),
				templateParams, schemeToOfficialNoticeFileNameMapper.map(scheme));
    }
	
	public void sendOfficialNotice(final Request request) {
        final ReissueRequestPayload requestPayload = (ReissueRequestPayload) request.getPayload();
		final List<FileInfoDTO> attachments = List.of(requestPayload.getOfficialNotice(),
				requestPayload.getDocument());
        officialNoticeSendService.sendOfficialNotice(attachments, request);
    }
}
