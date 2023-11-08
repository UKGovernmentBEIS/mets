package uk.gov.pmrv.api.mireport.common.verificationbodyusers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.mireport.aviation.AviationMiReportGeneratorHandler;
import uk.gov.pmrv.api.mireport.installation.InstallationMiReportGeneratorHandler;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerificationBodyUsersReportGeneratorHandlerHandler implements
    InstallationMiReportGeneratorHandler<EmptyMiReportParams>,
    AviationMiReportGeneratorHandler<EmptyMiReportParams> {

    private final VerificationBodyUsersRepository verificationBodyUsersRepository;

    private final UserAuthService userAuthService;

    @Override
    @Transactional(readOnly = true)
    public MiReportResult generateMiReport(EntityManager entityManager, EmptyMiReportParams reportParams) {

        List<VerificationBodyUser> verificationBodyUsers = verificationBodyUsersRepository.findAllVerificationBodyUsers(entityManager);
        Map<String, VerifierUserInfoDTO> verifierUsersInfo = getVerifierUserInfoByUserIds(verificationBodyUsers);

        List<VerificationBodyUser> payload = verificationBodyUsers.stream()
                .map(verificationBodyUser -> {
                    if (Optional.ofNullable(verificationBodyUser.getUserId()).isPresent()) {
                        VerifierUserInfoDTO verifierUserInfoDTO = verifierUsersInfo.get(verificationBodyUser.getUserId());
                        appendUserDetails(verificationBodyUser, verifierUserInfoDTO);
                    }
                    return verificationBodyUser;
                }).collect(Collectors.toList());

        return VerificationBodyUsersMiReportResult.builder()
                .reportType(getReportType())
                .columnNames(VerificationBodyUser.getColumnNames())
                .results(payload)
                .build();
    }

    @Override
    public MiReportType getReportType() {
        return MiReportType.LIST_OF_VERIFICATION_BODY_USERS;
    }

    private Map<String, VerifierUserInfoDTO> getVerifierUserInfoByUserIds(List<VerificationBodyUser> verificationBodyUsers) {

        List<String> userIds = verificationBodyUsers.stream()
                .map(VerificationBodyUser::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return userAuthService.getUsersWithAttributes(userIds, VerifierUserInfoDTO.class)
                .stream()
                .collect(Collectors.toMap(VerifierUserInfoDTO::getId, Function.identity()));
    }

    private void appendUserDetails(VerificationBodyUser verificationBodyUser, VerifierUserInfoDTO verifierUserInfoDTO) {
        verificationBodyUser.setVerifierFullName(verifierUserInfoDTO.getFullName());
        verificationBodyUser.setTelephone(verifierUserInfoDTO.getTelephone());
        verificationBodyUser.setLastLogon(Optional.ofNullable(verifierUserInfoDTO.getLastLoginDate())
                .map(VerificationBodyUsersReportGeneratorHandlerHandler::formatLastLoginDate).orElse(null));
        verificationBodyUser.setEmail(verifierUserInfoDTO.getEmail());
    }

    private static String formatLastLoginDate(String lastLoginDate) {
        return LocalDateTime.parse(lastLoginDate, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"));
    }
}
