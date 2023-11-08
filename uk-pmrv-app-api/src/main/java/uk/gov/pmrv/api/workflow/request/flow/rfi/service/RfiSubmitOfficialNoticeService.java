package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RfiSubmitOfficialNoticeService {
    
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;
    
    @Transactional
    public FileInfoDTO generateOfficialNotice(Request request, String signatory,
            UserInfoDTO accountPrimaryContact, List<String> ccRecipientsEmails) {
        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(
                DocumentTemplateParamsSourceData.builder()
                    .contextActionType(DocumentTemplateGenerationContextActionType.RFI_SUBMIT)
                    .request(request)
                    .signatory(signatory)
                    .accountPrimaryContact(accountPrimaryContact)
                    .toRecipientEmail(accountPrimaryContact.getEmail())
                    .ccRecipientsEmails(ccRecipientsEmails)
                    .build()
                );

        return documentFileGeneratorService.generateFileDocument(DocumentTemplateType.IN_RFI, templateParams,
                "Request for Further Information.pdf");
    }
    
    public void sendOfficialNotice(FileInfoDTO officialNotice, 
            Request request,
            List<String> ccRecipientsEmails) {
    	officialNoticeSendService.sendOfficialNotice(List.of(officialNotice), request, ccRecipientsEmails);
    }

}
