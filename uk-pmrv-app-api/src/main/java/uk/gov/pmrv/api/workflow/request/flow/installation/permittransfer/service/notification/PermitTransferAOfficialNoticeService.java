package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.service.DocumentFileGeneratorService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitTransferAOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestService requestService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final DocumentFileGeneratorService documentFileGeneratorService;
    private final OfficialNoticeSendService officialNoticeSendService;

    @Transactional
    public void generateAndSaveGrantedOfficialNotice(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        final String fileName = "permit_transfer_notice.pdf";

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
            accountPrimaryContact,
            List.of(),
            DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_ACCEPTED,
            DocumentTemplateType.PERMIT_TRANSFER_ACCEPTED,
            fileName);

        requestPayload.setOfficialNotice(officialNotice);
    }

    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
            accountPrimaryContact,
            List.of(),
            DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_REFUSED,
            DocumentTemplateType.PERMIT_TRANSFER_REFUSED,
            "permit_transfer_refused_notice.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    @Transactional
    public void generateAndSaveDeemedWithdrawnOfficialNotice(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();

        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        final FileInfoDTO officialNotice = this.generateOfficialNotice(request,
            accountPrimaryContact,
            List.of(),
            DocumentTemplateGenerationContextActionType.PERMIT_TRANSFER_A_DEEMED_WITHDRAWN,
            DocumentTemplateType.PERMIT_TRANSFER_DEEMED_WITHDRAWN,
            "permit_transfer_deemed_withdrawn_notice.pdf");

        requestPayload.setOfficialNotice(officialNotice);
    }

    private FileInfoDTO generateOfficialNotice(final Request request,
                                               final UserInfoDTO accountPrimaryContact,
                                               final List<String> ccRecipientsEmails,
                                               final DocumentTemplateGenerationContextActionType type,
                                               final DocumentTemplateType documentTemplateType,
                                               final String fileNameToGenerate) {

        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();

        final TemplateParams
            templateParams = constructTemplateParams(request, accountPrimaryContact, ccRecipientsEmails, type, requestPayload);
        return documentFileGeneratorService.generateFileDocument(documentTemplateType, templateParams, fileNameToGenerate);
    }

    private TemplateParams constructTemplateParams(final Request request,
                                                   final UserInfoDTO accountPrimaryContact,
                                                   final List<String> ccRecipientsEmails,
                                                   final DocumentTemplateGenerationContextActionType type,
                                                   final PermitTransferARequestPayload requestPayload) {

        final String receiverRequestId = requestPayload.getRelatedRequestId();
        final Request receiverRequest = requestService.findRequestById(receiverRequestId);
        final PermitTransferBRequestPayload receiverRequestPayload = (PermitTransferBRequestPayload) receiverRequest.getPayload();

        return documentTemplateOfficialNoticeParamsProvider
            .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                .contextActionType(type)
                .request(request)
                .signatory(receiverRequestPayload.getDecisionNotification().getSignatory())
                .accountPrimaryContact(accountPrimaryContact)
                .toRecipientEmail(accountPrimaryContact.getEmail())
                .ccRecipientsEmails(ccRecipientsEmails).build());
    }

    public void sendOfficialNotice(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitTransferARequestPayload requestPayload = (PermitTransferARequestPayload) request.getPayload();

        final String serviceContact =
            requestAccountContactQueryService.getRequestAccountContact(request, AccountContactType.SERVICE)
                .map(UserInfoDTO::getEmail)
                .orElse(null);

        final List<String> toRecipientsEmails = serviceContact != null ? List.of(serviceContact) : List.of();

        final List<FileInfoDTO> attachments = List.of(requestPayload.getOfficialNotice());
        officialNoticeSendService.sendOfficialNotice(attachments, request, toRecipientsEmails, List.of());
    }
}
