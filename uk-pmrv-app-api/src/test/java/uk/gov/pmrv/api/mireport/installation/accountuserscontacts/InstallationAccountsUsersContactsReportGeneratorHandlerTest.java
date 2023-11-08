package uk.gov.pmrv.api.mireport.installation.accountuserscontacts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUserContact;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.OperatorUserInfoDTO;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountsUsersContactsReportGeneratorHandlerTest {

    @InjectMocks
    private InstallationAccountUsersContactsReportGeneratorHandler service;

    @Mock
    private InstallationAccountUsersContactsRepository accountUsersContactsRepository;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private EntityManager entityManager;

    @Test
    void getReportType() {
        assertThat(service.getReportType()).isEqualTo(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS);
    }

    @ParameterizedTest
    @MethodSource("permitValues")
    void generateMiReport(String permitId, PermitType permitType) {
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().build();
        String loginDate = "2022-08-18T16:48:14.729098Z";
        String parsedLoginDate = LocalDateTime.parse(loginDate, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"));
        AccountUserContact accountUserContact = AccountUserContact.builder()
                .userId("userId")
                .accountId("emitterId")
                .accountName("accountName")
                .accountStatus("accountStatus")
                .permitId(permitId)
                .permitType(Optional.ofNullable(permitType).map(Enum::name).orElse(null))
                .legalEntityName("legalEntityName")
                .primaryContact(true)
                .secondaryContact(true)
                .financialContact(true)
                .serviceContact(true)
                .authorityStatus("authorityStatus")
                .role("role")
                .build();
        OperatorUserInfoDTO operatorUserInfoDTO = OperatorUserInfoDTO.builder()
                .id("userId")
                .firstName("firstname")
                .lastName("lastname")
                .email("test@test.com")
                .phoneNumber("6939")
                .phoneNumberCode("30")
                .lastLoginDate(loginDate)
                .build();
        AccountUserContact accountUserContactExpected = AccountUserContact.builder()
                .userId("userId")
                .accountId("emitterId")
                .accountName("accountName")
                .accountStatus("accountStatus")
                .permitId(permitId)
                .permitType(Optional.ofNullable(permitType).map(Enum::name).orElse(null))
                .legalEntityName("legalEntityName")
                .primaryContact(true)
                .secondaryContact(true)
                .financialContact(true)
                .serviceContact(true)
                .authorityStatus("authorityStatus")
                .role("Operator")
                .name(operatorUserInfoDTO.getFullName())
                .email("test@test.com")
                .telephone(operatorUserInfoDTO.getTelephone())
                .lastLogon(parsedLoginDate)
                .role("role")
                .build();

        when(userAuthService.getUsersWithAttributes(Collections.singletonList("userId"), OperatorUserInfoDTO.class)).thenReturn(
                Collections.singletonList(operatorUserInfoDTO));
        when(accountUsersContactsRepository.findAccountUserContacts(entityManager))
            .thenReturn(Collections.singletonList(accountUserContact));
        AccountsUsersContactsMiReportResult report = (AccountsUsersContactsMiReportResult) service.generateMiReport(entityManager, reportParams);

        assertThat(report.getResults()).hasSize(1);
        AccountUserContact accountUserContactActual = report.getResults().get(0);

        Assertions.assertEquals(accountUserContactExpected, accountUserContactActual);
    }

    private static Stream<Arguments> permitValues() {
        return Stream.of(
                Arguments.of("permitId", PermitType.GHGE),
                Arguments.of("permitId", PermitType.HSE),
                Arguments.of(null, PermitType.GHGE),
                Arguments.of(null, PermitType.HSE),
                Arguments.of(null, null)
        );
    }

}
