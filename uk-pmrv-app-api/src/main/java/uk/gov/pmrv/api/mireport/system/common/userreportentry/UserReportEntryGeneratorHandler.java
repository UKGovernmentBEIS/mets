package uk.gov.pmrv.api.mireport.system.common.userreportentry;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE;
import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_USER_ROLE_CODE;

@Service
@RequiredArgsConstructor
public abstract class UserReportEntryGeneratorHandler {

    protected final UserAuthService userAuthService;
    protected final UserReportEntryRepository userReportEntryRepository;
    protected final AuthorityRepository authorityRepository;

    protected List<UserReportEntry> generate(EntityManager entityManager, AccountType accountType) {
        List<UserReportEntry> userReportEntries = userReportEntryRepository.findUserReportEntries(entityManager, accountType);

        List<Authority> regulatorAuthorities = authorityRepository.findByCompetentAuthorityIsNotNull();
        if (!regulatorAuthorities.isEmpty()) {
            userReportEntries.addAll(authoritiesToUserReportEntries(regulatorAuthorities));
        }
        List<Authority> verifierAuthorities = authorityRepository.findByCodeIn(List.of(VERIFIER_ADMIN_ROLE_CODE, VERIFIER_USER_ROLE_CODE));
        if (!verifierAuthorities.isEmpty()) {
            userReportEntries.addAll(authoritiesToUserReportEntries(verifierAuthorities));
        }

        Map<String, UserReportInfoDTO> userReportInfoDTOMap = getUserReportInfoByUserIds(userReportEntries);

        return userReportEntries.stream()
                .map(userReportEntry -> {
                    Optional.ofNullable(userReportEntry.getUserAccountId())
                            .map(userReportInfoDTOMap::get)
                            .ifPresent(userReportInfoDTO ->
                                    appendUserDetails(userReportEntry, userReportInfoDTO));
                    return userReportEntry;
                }).toList();
    }

    protected List<UserReportEntry> authoritiesToUserReportEntries(List<Authority> authorities) {
        return authorities.stream()
                .map(authority -> UserReportEntry.builder()
                        .userAccountId(authority.getUserId())
                        .role(authority.getCode() != null ? authority.getCode() : null)
                        .userAccountStatus(authority.getStatus() != null ? authority.getStatus().name() : null)
                        .contactTypes(Collections.emptyList())
                        .build())
                .toList();
    }

    protected Map<String, UserReportInfoDTO> getUserReportInfoByUserIds(List<UserReportEntry> userReportEntries) {
        List<String> userIds = userReportEntries.stream()
                .map(UserReportEntry::getUserAccountId)
                .filter(Objects::nonNull)
                .toList();

        return getUsersWithAttributes(userIds);
    }

    protected Map<String, UserReportInfoDTO> getUsersWithAttributes(List<String> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userAuthService.getUsersWithAttributes(userIds, UserReportInfoDTO.class)
                .stream()
                .collect(Collectors.toMap(UserReportInfoDTO::getId, Function.identity()));
    }

    protected void appendUserDetails(UserReportEntry userReportEntry, UserReportInfoDTO userReportInfoDTO) {
        if (userReportInfoDTO != null) {
            userReportEntry.setFullName(userReportInfoDTO.getFullName());
            userReportEntry.setTelephone(userReportInfoDTO.getTelephone());
            userReportEntry.setLastLogin(formatLastLoginDate(userReportInfoDTO.getLastLoginDate()));
            userReportEntry.setEmail(userReportInfoDTO.getEmail());
        }
    }

    protected String formatLastLoginDate(String lastLoginDate) {
        return Optional.ofNullable(lastLoginDate)
                .map(date -> LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")))
                .orElse(null);
    }

    protected UsersMiReportResult buildResult(List<UserReportEntry> payload, String reportType) {
        return UsersMiReportResult.builder()
                .reportType(reportType)
                .columnNames(UserReportEntry.getColumnNames())
                .results(payload)
                .build();
    }
}
