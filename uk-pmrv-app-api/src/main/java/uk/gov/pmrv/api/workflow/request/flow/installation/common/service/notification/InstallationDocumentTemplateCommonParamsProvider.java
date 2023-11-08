package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityService;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateCommonParamsAbstractProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;

@Service
public class InstallationDocumentTemplateCommonParamsProvider extends DocumentTemplateCommonParamsAbstractProvider {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final PermitQueryService permitQueryService;
    private final DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;
    private final AccountContactQueryService accountContactQueryService;
    private final UserAuthService userAuthService;


    public InstallationDocumentTemplateCommonParamsProvider(RegulatorUserAuthService regulatorUserAuthService, UserAuthService userAuthService,
                                                            AppProperties appProperties, DateService dateService,
                                                            CompetentAuthorityService competentAuthorityService,
                                                            InstallationAccountQueryService installationAccountQueryService,
                                                            PermitQueryService permitQueryService,
                                                            DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver,
                                                            AccountContactQueryService accountContactQueryService) {
        super(regulatorUserAuthService, userAuthService, appProperties, dateService, competentAuthorityService);
        this.installationAccountQueryService = installationAccountQueryService;
        this.permitQueryService = permitQueryService;
        this.documentTemplateLocationInfoResolver = documentTemplateLocationInfoResolver;
        this.accountContactQueryService = accountContactQueryService;
        this.userAuthService = userAuthService;
    }

    @Override
    public String getPermitReferenceId(Long accountId) {
        return permitQueryService.getPermitIdByAccountId(accountId).orElse(null);
    }

    @Override
    public AccountTemplateParams getAccountTemplateParams(Long accountId) {
        final InstallationAccountWithoutLeHoldingCompanyDTO account = installationAccountQueryService.getAccountWithoutLeHoldingCompanyDTOById(accountId);
        final UserInfoDTO primaryContact = accountContactQueryService
            .findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY)
            .map(userAuthService::getUserByUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        final UserInfoDTO serviceContact = accountContactQueryService
            .findContactByAccountAndContactType(accountId, AccountContactType.SERVICE)
            .map(userAuthService::getUserByUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return InstallationAccountTemplateParams.builder()
            .name(account.getName())
            .competentAuthority(account.getCompetentAuthority())
            .accountType(account.getAccountType())
            .emitterType(account.getEmitterType() != null ? account.getEmitterType().name() : null)
            .siteName(account.getSiteName())
            .location(documentTemplateLocationInfoResolver.constructLocationInfo(account.getLocation()))
            .legalEntityName(account.getLegalEntity().getName())
            .legalEntityLocation(documentTemplateLocationInfoResolver.constructAddressInfo(account.getLegalEntity().getAddress()))
            .primaryContact(primaryContact.getFullName())
            .primaryContactEmail(primaryContact.getEmail())
            .serviceContact(serviceContact.getFullName())
            .serviceContactEmail(serviceContact.getEmail())
            .installationCategory(account.getInstallationCategory() != null ? account.getInstallationCategory().getDescription() : null)
            .build();
    }

	@Override
	public AccountType getAccountType() {
		return AccountType.INSTALLATION;
	}
}
