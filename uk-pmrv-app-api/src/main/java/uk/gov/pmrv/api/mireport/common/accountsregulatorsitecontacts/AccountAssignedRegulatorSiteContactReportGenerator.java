package uk.gov.pmrv.api.mireport.common.accountsregulatorsitecontacts;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AccountAssignedRegulatorSiteContactReportGenerator {
    private final UserAuthService userAuthService;

    public abstract List<AccountAssignedRegulatorSiteContact> findAccountAssignedRegulatorSiteContacts(EntityManager entityManager);


    public MiReportType getReportType() {
        return MiReportType.LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS;
    }

    @Transactional(readOnly = true)
    public MiReportResult generateMiReport(EntityManager entityManager, EmptyMiReportParams reportParams) {
        List<AccountAssignedRegulatorSiteContact> accountAssignedRegulatorSiteContacts =
            findAccountAssignedRegulatorSiteContacts(entityManager);
        Map<String, UserInfo> userInfoMap = getUserInfoByUserIds(accountAssignedRegulatorSiteContacts);

        List<AccountAssignedRegulatorSiteContact> results = accountAssignedRegulatorSiteContacts.stream()
            .map(accountAssignedRegulatorSiteContact -> {
                if (Optional.ofNullable(accountAssignedRegulatorSiteContact.getUserId()).isPresent()) {
                    UserInfo userInfo = userInfoMap.get(accountAssignedRegulatorSiteContact.getUserId());
                    appendUserName(accountAssignedRegulatorSiteContact, userInfo);
                }
                return accountAssignedRegulatorSiteContact;
            }).collect(Collectors.toList());

        return AccountAssignedRegulatorSiteContactsMiReportResult.builder()
            .reportType(getReportType())
            .columnNames(AccountAssignedRegulatorSiteContact.getColumnNames())
            .results(results)
            .build();
    }

    private Map<String, UserInfo> getUserInfoByUserIds(List<AccountAssignedRegulatorSiteContact> accountAssignedRegulatorSiteContacts) {
        List<String> userIds = accountAssignedRegulatorSiteContacts
            .stream()
            .map(AccountAssignedRegulatorSiteContact::getUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return userAuthService.getUsers(userIds).stream()
            .collect(Collectors.toMap(UserInfo::getId, Function.identity()));
    }

    private void appendUserName(AccountAssignedRegulatorSiteContact accountAssignedRegulatorSiteContact, UserInfo userInfo) {
        accountAssignedRegulatorSiteContact.setAssignedRegulatorName(userInfo.getFullName());
    }
}
