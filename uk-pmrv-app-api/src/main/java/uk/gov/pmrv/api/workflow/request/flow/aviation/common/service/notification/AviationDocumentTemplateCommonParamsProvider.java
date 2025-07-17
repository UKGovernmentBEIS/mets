package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.config.CompetentAuthorityProperties;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.notification.template.aviation.domain.AviationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.AccountTemplateParams;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CompetentAuthorityDTOByRequestResolverDelegator;
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
                                                        CompetentAuthorityProperties competentAuthorityProperties, DateService dateService,
                                                        EmissionsMonitoringPlanQueryService empQueryService,
                                                        AviationAccountQueryService aviationAccountQueryService,
                                                        DocumentTemplateLocationInfoResolver documentTemplateLocationInfoResolver,
                                                        AccountContactQueryService accountContactQueryService,
                                                        CompetentAuthorityDTOByRequestResolverDelegator competentAuthorityDTOByRequestResolverDelegator) {
        super(regulatorUserAuthService, userAuthService, competentAuthorityProperties, dateService, competentAuthorityDTOByRequestResolverDelegator);
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
