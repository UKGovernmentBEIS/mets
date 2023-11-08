package uk.gov.pmrv.api.mireport.common.accountuserscontacts;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AccountUsersContactsReportGenerator {
    private final UserAuthService userAuthService;

    public abstract List<AccountUserContact> findAccountUserContacts(EntityManager entityManager);

    public MiReportType getReportType() {
        return MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
    }

    @Transactional(readOnly = true)
    public MiReportResult generateMiReport(EntityManager entityManager, EmptyMiReportParams reportParams) {
        List<AccountUserContact> accountUserContacts = findAccountUserContacts(entityManager);
        Map<String, OperatorUserInfoDTO> operatorUsersInfo = getOperatorUserInfoByUserIds(accountUserContacts);

        List<AccountUserContact> results = accountUserContacts.stream()
            .map(accountUserContact -> {
                OperatorUserInfoDTO operatorUserInfo = operatorUsersInfo.get(accountUserContact.getUserId());
                appendUserDetails(accountUserContact, operatorUserInfo);
                return accountUserContact;
            }).collect(Collectors.toList());

        return AccountsUsersContactsMiReportResult.builder()
            .reportType(getReportType())
            .columnNames(AccountUserContact.getColumnNames())
            .results(results)
            .build();
    }

    private void appendUserDetails(AccountUserContact accountUserContact, OperatorUserInfoDTO operatorUserInfo) {
        if (Optional.ofNullable(operatorUserInfo).isPresent()) {
            accountUserContact.setName(operatorUserInfo.getFullName());
            accountUserContact.setTelephone(operatorUserInfo.getTelephone());
            accountUserContact.setLastLogon(Optional.ofNullable(operatorUserInfo.getLastLoginDate())
                .map(AccountUsersContactsReportGenerator::formatLastLoginDate).orElse(null));
            accountUserContact.setEmail(operatorUserInfo.getEmail());
        }
    }

    private Map<String, OperatorUserInfoDTO> getOperatorUserInfoByUserIds(List<AccountUserContact> accountUserContacts) {
        List<String> userIds = accountUserContacts.stream().map(AccountUserContact::getUserId).collect(Collectors.toList());
        return userAuthService.getUsersWithAttributes(userIds, OperatorUserInfoDTO.class)
            .stream()
            .collect(Collectors.toMap(OperatorUserInfoDTO::getId, Function.identity()));
    }

    private static String formatLastLoginDate(String lastLoginDate) {
        return LocalDateTime.parse(lastLoginDate, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"));
    }
}
