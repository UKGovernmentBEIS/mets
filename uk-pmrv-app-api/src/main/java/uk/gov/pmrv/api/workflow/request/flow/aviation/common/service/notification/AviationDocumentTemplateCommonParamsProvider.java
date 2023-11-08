package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateCommonParamsAbstractProvider;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateLocationInfoResolver;

import java.util.Optional;

@Service
public class AviationDocumentTemplateCommonParamsProvider extends DocumentTemplateCommonParamsAbstractProvider {

    private final EmissionsMonitoringPlanQueryService empQueryService;
    private final AviationAccountQueryService aviationAccountQueryService;
    private final DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver;
    private final AccountContactQueryService accountContactQueryService;
    private final UserAuthService userAuthService;

    public AviationDocumentTemplateCommonParamsProvider(RegulatorUserAuthService regulatorUserAuthService, UserAuthService userAuthService,
                                                        AppProperties appProperties, DateService dateService,
                                                        CompetentAuthorityService competentAuthorityService,
                                                        EmissionsMonitoringPlanQueryService empQueryService,
                                                        AviationAccountQueryService aviationAccountQueryService,
                                                        DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver,
                                                        AccountContactQueryService accountContactQueryService) {
        super(regulatorUserAuthService, userAuthService, appProperties, dateService, competentAuthorityService);
        this.empQueryService = empQueryService;
        this.aviationAccountQueryService = aviationAccountQueryService;
        this.documentTemplateLocationInfoResolver = documentTemplateLocationInfoResolver;
        this.accountContactQueryService = accountContactQueryService;
        this.userAuthService = userAuthService;
    }


    @Override
    public String getPermitReferenceId(Long accountId) {
        return empQueryService.getEmpIdByAccountId(accountId).orElse(null);
    }

    @Override
    public AccountTemplateParams getAccountTemplateParams(Long accountId) {
        final AviationAccountInfoDTO accountInfo = aviationAccountQueryService.getAviationAccountInfoDTOById(accountId);
        final Optional<UserInfoDTO> primaryContact = accountContactQueryService
            .findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY)
            .map(userAuthService::getUserByUserId);
        final Optional<UserInfoDTO> serviceContact = accountContactQueryService
            .findContactByAccountAndContactType(accountId, AccountContactType.SERVICE)
            .map(userAuthService::getUserByUserId);

        return AviationAccountTemplateParams.builder()
            .name(accountInfo.getName())
            .competentAuthority(accountInfo.getCompetentAuthority())
            .location(documentTemplateLocationInfoResolver.constructLocationInfo(accountInfo.getLocation()))
            .accountType(getAccountType())
            .crcoCode(accountInfo.getCrcoCode())
            .primaryContact(primaryContact.map(UserInfoDTO::getFullName).orElse(null))
            .primaryContactEmail(primaryContact.map(UserInfoDTO::getEmail).orElse(null))
            .serviceContact(serviceContact.map(UserInfoDTO::getFullName).orElse(null))
            .serviceContactEmail(serviceContact.map(UserInfoDTO::getEmail).orElse(null))
            .build();
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.AVIATION;
    }
}
