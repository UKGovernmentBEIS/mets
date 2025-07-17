package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpIssuanceRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateOfficialNoticeParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateParamsSourceData;

@Service
@RequiredArgsConstructor
public class AviationPreviewOfficialNoticeService {

    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final DocumentTemplateOfficialNoticeParamsProvider documentTemplateOfficialNoticeParamsProvider;
    private final EmpIssuanceRequestIdGenerator generator;
    private final EmissionsMonitoringPlanQueryService empQueryService;

    public TemplateParams generateCommonParamsWithoutAccountNameLocation(final Request request,
                                                                         final DecisionNotification decisionNotification) {
        final TemplateParams templateParams = generateCommonParams(request, decisionNotification, true);

        // the following information may be invalid as it is filled by the account entity which has not 
        // been updated yet at the time of the preview. Thus, it is set to null
        templateParams.getAccountParams().setName(null);
        templateParams.getAccountParams().setLocation(null);

        return templateParams;
    }
    
	public TemplateParams generateCommonParams(final Request request, final DecisionNotification decisionNotification,
			boolean primaryContactRequired) {
		final Optional<UserInfoDTO> accountPrimaryContactOpt = requestAccountContactQueryService
				.getRequestAccountPrimaryContact(request);
		
		if(primaryContactRequired && accountPrimaryContactOpt.isEmpty()) {
			throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND);
		}
		
        final List<String> ccRecipientsEmails =
                decisionNotificationUsersService.findUserEmails(decisionNotification);
        final String signatory = decisionNotification.getSignatory();

        final TemplateParams templateParams = documentTemplateOfficialNoticeParamsProvider
                .constructTemplateParams(DocumentTemplateParamsSourceData.builder()
                        .request(request)
                        .signatory(signatory)
                        .accountPrimaryContact(accountPrimaryContactOpt.orElse(null))
                        .toRecipientEmail(accountPrimaryContactOpt.map(UserInfoDTO::getEmail).orElse(null))
                        .ccRecipientsEmails(ccRecipientsEmails).build());

        // manually set emp id as it might not exist yet (e.g. emp issuance accepted official letter)
        final Long accountId = request.getAccountId();
        final String empId = empQueryService.getEmpIdByAccountId(accountId).orElse(
                generator.generate(RequestParams.builder().accountId(accountId).build())
        );
        templateParams.setPermitId(empId);

        return templateParams;
    }
}
