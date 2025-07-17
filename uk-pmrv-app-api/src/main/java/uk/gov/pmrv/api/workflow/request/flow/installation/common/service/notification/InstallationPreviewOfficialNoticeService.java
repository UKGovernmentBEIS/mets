package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.permit.service.PermitIdentifierGenerator;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationPreviewOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final PermitIdentifierGenerator generator;
    private final PermitQueryService permitQueryService;

    public TemplateParams generateCommonParams(final Request request,
                                               final DecisionNotification decisionNotification) {

        final TemplateParams templateParams = generateCommonParamsWithExtraAccountDetails(request, decisionNotification);
        
        // the following information may be invalid as it is filled by the account entity which has not 
        // been updated yet at the time of the preview. Thus, it is set to null
        ((InstallationAccountTemplateParams) templateParams.getAccountParams()).setInstallationCategory(null);
        ((InstallationAccountTemplateParams) templateParams.getAccountParams()).setEmitterType(null);

        return templateParams;
    }

    public TemplateParams generateCommonParamsWithExtraAccountDetails(final Request request,
                                                                      final DecisionNotification decisionNotification) {
        final UserInfoDTO accountPrimaryContact =
                requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));
        final List<String> ccRecipientsEmails =
                decisionNotificationUsersService.findUserEmails(decisionNotification);
        final String signatory = decisionNotification.getSignatory();

        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
                .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                        .request(request)
                        .signatory(signatory)
                        .accountPrimaryContact(accountPrimaryContact)
                        .toRecipientEmail(accountPrimaryContact.getEmail())
                        .ccRecipientsEmails(ccRecipientsEmails).build());

        // manually set permit id as it might not exist yet (e.g. permit issuance accepted official letter)
        final Long accountId = request.getAccountId();
        final String permitId = permitQueryService.getPermitIdByAccountId(accountId).orElse(
                generator.generate(accountId)
        );
        templateParams.setPermitId(permitId);

        return templateParams;
    }
}
