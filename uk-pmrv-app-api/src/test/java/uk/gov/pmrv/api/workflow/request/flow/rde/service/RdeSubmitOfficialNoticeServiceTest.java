package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RdeSubmitOfficialNoticeServiceTest {

    @InjectMocks
    private RdeSubmitOfficialNoticeService service;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    
    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;
    
    @Mock
    private OfficialNoticeSendService officialNoticeSendService;
    
    @Test
    void generateOfficialNotice() {
        final Request request = Request.builder()
                .accountId(1L)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
                .firstName("fn").lastName("ln").email("email@email")
                .build();
        
        List<String> ccRecipientsEmails = List.of("cc1@email", "cc2@email");
        
        DocumentTemplateParamsSourceData templateSourceParams = DocumentTemplateParamsSourceData.builder()
                .request(request)
                .contextActionType(DocumentTemplateGenerationContextActionType.RDE_SUBMIT)
                .accountPrimaryContact(accountPrimaryContact)
                .signatory("signatory")
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .ccRecipientsEmails(ccRecipientsEmails)
                .build();
        TemplateParams templateParams = TemplateParams.builder().build();
        
        
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(templateSourceParams))
            .thenReturn(templateParams);
        
        //invoke
        service.generateOfficialNotice(request, "signatory", accountPrimaryContact, ccRecipientsEmails);
        
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(templateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(DocumentTemplateType.IN_RDE, templateParams, "Request for Determination Extension.pdf");
    }
    
    @Test
    void sendOfficialNotice() {
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("official_doc.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        
        Request request = Request.builder()
                .accountId(1L)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        
        List<String> ccRecipientsEmails = List.of("cc1@email", "cc2@email", "email@email");
        
        // invoke
        service.sendOfficialNotice(officialNotice, request, ccRecipientsEmails);
        verify(officialNoticeSendService, times(1)).sendOfficialNotice(List.of(officialNotice), request, ccRecipientsEmails);
    }
}
