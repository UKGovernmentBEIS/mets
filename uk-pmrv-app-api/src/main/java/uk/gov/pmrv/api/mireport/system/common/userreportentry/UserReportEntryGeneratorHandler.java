package uk.gov.pmrv.api.mireport.system.common.userreportentry;

import jakarta.persistence.EntityManager;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Collections;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE;
import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_USER_ROLE_CODE;

@Service
@RequiredArgsConstructor
public abstract class UserReportEntryGeneratorHandler {

    protected final UserAuthService userAuthService;
    protected final UserReportEntryRepository userReportEntryRepository;

    protected List<UserReportEntry> generate(EntityManager entityManager, AccountType accountType) {
        List<UserReportEntry> userReportEntries = userReportEntryRepository.findUserReportEntries(entityManager, accountType);

        List<Authority> regulatorAuthorities = userReportEntryRepository.findByCompetentAuthorityIsNotNull(entityManager);
        if (!regulatorAuthorities.isEmpty()) {
            userReportEntries.addAll(authoritiesToUserReportEntries(regulatorAuthorities));
        }
        List<Authority> verifierAuthorities = userReportEntryRepository.findByCodeIn(entityManager, List.of(VERIFIER_ADMIN_ROLE_CODE, VERIFIER_USER_ROLE_CODE));
        if (!verifierAuthorities.isEmpty()) {
            userReportEntries.addAll(authoritiesToUserReportEntries(verifierAuthorities));
        }

        Map<String, UserReportInfoDTO> userReportInfoDTOMap = getUserReportInfoByUserIds(userReportEntries);

        List<UserReportEntry> enriched = userReportEntries.stream()
                .map(userReportEntry -> {
                    Optional.ofNullable(userReportEntry.getUserAccountId())
                            .map(userReportInfoDTOMap::get)
                            .ifPresent(userReportInfoDTO ->
                                    appendUserDetails(userReportEntry, userReportInfoDTO));
                    return userReportEntry;
                }).toList();

        return consolidateByUserId(enriched);
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
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss", Locale.ROOT)))
                .orElse(null);
    }

    protected List<UserReportEntry> consolidateByUserId(List<UserReportEntry> entries) {
        Map<String, UserReportEntry> consolidated = new LinkedHashMap<>();
        for (UserReportEntry entry : entries) {
            consolidated.merge(entry.getUserAccountId(), toInitialEntry(entry), this::mergeEntries);
        }
        consolidated.values().forEach(this::normalizeBlankStrings);
        return new ArrayList<>(consolidated.values());
    }

    private UserReportEntry toInitialEntry(UserReportEntry entry) {
        return UserReportEntry.builder()
                .userAccountId(entry.getUserAccountId())
                .fullName(entry.getFullName())
                .email(entry.getEmail())
                .telephone(entry.getTelephone())
                .mobile(entry.getMobile())
                .lastLogin(entry.getLastLogin())
                .role(Objects.toString(entry.getRole(), ""))
                .userAccountStatus(Objects.toString(entry.getUserAccountStatus(), ""))
                .contactTypes(new ArrayList<>(Optional.ofNullable(entry.getContactTypes()).orElse(Collections.emptyList())))
                .build();
    }

    private UserReportEntry mergeEntries(UserReportEntry existing, UserReportEntry incoming) {
        existing.setRole(mergeCsv(existing.getRole(), incoming.getRole()));
        existing.setUserAccountStatus(mergeCsv(existing.getUserAccountStatus(), incoming.getUserAccountStatus()));
        existing.setContactTypes(mergeContactTypes(existing.getContactTypes(), incoming.getContactTypes()));
        return existing;
    }

    private String mergeCsv(String existing, String incoming) {
        if (incoming == null || incoming.isBlank()) return existing;
        TreeSet<String> parts = splitCsv(existing);
        parts.add(incoming.trim());
        return String.join(", ", parts);
    }

    private List<String> mergeContactTypes(List<String> existing, List<String> incoming) {
        if (incoming == null) return existing;
        TreeSet<String> types = new TreeSet<>(Optional.ofNullable(existing).orElse(Collections.emptyList()));
        types.addAll(incoming);
        return new ArrayList<>(types);
    }

    private void normalizeBlankStrings(UserReportEntry entry) {
        if (entry.getRole().isBlank()) entry.setRole(null);
        if (entry.getUserAccountStatus().isBlank()) entry.setUserAccountStatus(null);
    }

    private TreeSet<String> splitCsv(String value) {
        if (value == null || value.isBlank()) return new TreeSet<>();
        return java.util.Arrays.stream(value.split(",\\s*"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    protected UsersMiReportResult buildResult(List<UserReportEntry> payload, String reportType) {
        return UsersMiReportResult.builder()
                .reportType(reportType)
                .columnNames(UserReportEntry.getColumnNames())
                .results(payload)
                .build();
    }
}
