package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitTransferAOfficialNoticeServiceTest {

    @InjectMocks
    private PermitTransferAOfficialNoticeService service;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestService requestService;

    @Mock
    private OfficialNoticeSendService officialNoticeSendService;

    @Mock
    private DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;

    @Mock
    private DocumentFileGeneratorService documentFileGeneratorService;


    @Test
    void generateAndSaveGrantedOfficialNotice() {

        String requestId = "1";
        String fileName = "permit_transfer_notice.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        final String receiverRequestId = "receiverRequestId";
        PermitTransferARequestPayload transfererPayload = PermitTransferARequestPayload.builder().relatedRequestId(
            receiverRequestId).build();
        PermitTransferBRequestPayload receiverPayload = PermitTransferBRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request receiverRequest = Request.builder().payload(receiverPayload).build();
        Request transfererRequest = Request.builder().payload(transfererPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_ACCEPTED)
                .request(receiverRequest)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(transfererRequest);
        when(requestService.findRequestById(receiverRequestId)).thenReturn(receiverRequest);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(transfererRequest))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
            DocumentTemplateType.PERMIT_TRANSFER_ACCEPTED, templateParams, fileName))
            .thenReturn(officialDocFileInfoDTO);

        // Invoke
        service.generateAndSaveGrantedOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(receiverRequestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(transfererRequest);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
            DocumentTemplateType.PERMIT_TRANSFER_ACCEPTED, templateParams, fileName);

        assertThat(transfererPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void generateAndSaveRejectedOfficialNotice() {
        
        String requestId = "1";
        String fileName = "permit_transfer_refused_notice.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        final String receiverRequestId = "receiverRequestId";
        PermitTransferARequestPayload transfererPayload = PermitTransferARequestPayload.builder().relatedRequestId(
            receiverRequestId).build();
        PermitTransferBRequestPayload receiverPayload = PermitTransferBRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request receiverRequest = Request.builder().payload(receiverPayload).build();
        Request transfererRequest = Request.builder().payload(transfererPayload).build();
        
        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_REFUSED)
                .request(receiverRequest)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(transfererRequest);
        when(requestService.findRequestById(receiverRequestId)).thenReturn(receiverRequest);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(transfererRequest))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
            DocumentTemplateType.PERMIT_TRANSFER_REFUSED, templateParams, fileName))
            .thenReturn(officialDocFileInfoDTO);

        // Invoke
        service.generateAndSaveRejectedOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(receiverRequestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(transfererRequest);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
            DocumentTemplateType.PERMIT_TRANSFER_REFUSED, templateParams, fileName);

        assertThat(transfererPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }

    @Test
    void generateAndSaveDeemedWithdrawnOfficialNotice() {
        
        String requestId = "1";
        String fileName = "permit_transfer_deemed_withdrawn_notice.pdf";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .build();
        final String receiverRequestId = "receiverRequestId";
        PermitTransferARequestPayload transfererPayload = PermitTransferARequestPayload.builder().relatedRequestId(
            receiverRequestId).build();
        PermitTransferBRequestPayload receiverPayload = PermitTransferBRequestPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        Request receiverRequest = Request.builder().payload(receiverPayload).build();
        Request transfererRequest = Request.builder().payload(transfererPayload).build();

        TemplateParams templateParams = TemplateParams.builder().build();
        FileInfoDTO officialDocFileInfoDTO = buildOfficialFileInfo();

        UserInfoDTO accountPrimaryContactInfo = UserInfoDTO.builder().email("user@pmrv.uk").build();
        DocumentTemplateParamsSourceData documentTemplateSourceParams =
            DocumentTemplateParamsSourceData.builder()
                .contextActionType(DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_DEEMED_WITHDRAWN)
                .request(receiverRequest)
                .signatory(decisionNotification.getSignatory())
                .accountPrimaryContact(accountPrimaryContactInfo)
                .toRecipientEmail(accountPrimaryContactInfo.getEmail())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(transfererRequest);
        when(requestService.findRequestById(receiverRequestId)).thenReturn(receiverRequest);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(transfererRequest))
            .thenReturn(Optional.of(accountPrimaryContactInfo));
        when(documentTemplateOfficialNoticeParamsProvider.constructTemplateParams(documentTemplateSourceParams))
            .thenReturn(templateParams);
        when(documentFileGeneratorService.generateAndSaveFileDocument(
            DocumentTemplateType.PERMIT_TRANSFER_DEEMED_WITHDRAWN, templateParams, fileName))
            .thenReturn(officialDocFileInfoDTO);

        // Invoke
        service.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(receiverRequestId);
        verify(requestAccountContactQueryService, times(1)).getRequestAccountPrimaryContact(transfererRequest);
        verify(documentTemplateOfficialNoticeParamsProvider, times(1)).constructTemplateParams(documentTemplateSourceParams);
        verify(documentFileGeneratorService, times(1)).generateAndSaveFileDocument(
            DocumentTemplateType.PERMIT_TRANSFER_DEEMED_WITHDRAWN, templateParams, fileName);

        assertThat(transfererPayload.getOfficialNotice()).isEqualTo(officialDocFileInfoDTO);
    }
    
    @Test
    void sendOfficialNotice() {
        final String requestId = "requestId";
        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("officialNotice").build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(PermitTransferARequestPayload.builder()
                .officialNotice(officialNotice)
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.sendOfficialNotice(requestId);

        verify(officialNoticeSendService, times(1))
            .sendOfficialNotice(List.of(officialNotice), request);

    }

    private FileInfoDTO buildOfficialFileInfo() {
        return FileInfoDTO.builder()
            .name("offDoc.pdf")
            .uuid(UUID.randomUUID().toString())
            .build();
    }
}
