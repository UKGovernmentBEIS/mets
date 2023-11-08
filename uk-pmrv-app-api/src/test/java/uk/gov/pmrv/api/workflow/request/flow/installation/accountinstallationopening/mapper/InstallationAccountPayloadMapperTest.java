package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class InstallationAccountPayloadMapperTest {

    private InstallationAccountPayloadMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(InstallationAccountPayloadMapper.class);
    }

    @Test
    void toAccountDTO() {
        final String accountName = "accountName";
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final EmissionTradingScheme ets = EmissionTradingScheme.EU_ETS_INSTALLATIONS;
        final String leName = "leName";
        InstallationAccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        AccountDTO accountDTO = mapper.toAccountInstallationDTO(accountPayload);

        assertThat(accountDTO.getName()).isEqualTo(accountName);
        assertThat(accountDTO.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(accountDTO.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(accountDTO.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningRequestPayload() {
        final String accountName = "account";
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        final EmissionTradingScheme ets = EmissionTradingScheme.EU_ETS_INSTALLATIONS;
        final String leName = "le";
        final String requestAssigneeUser = "requestAssignee";
        InstallationAccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningRequestPayload requestPayload =
            mapper.toInstallationAccountOpeningRequestPayload(accountPayload, requestAssigneeUser);

        assertThat(requestPayload.getPayloadType()).isEqualTo(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD);
        assertThat((requestPayload.getOperatorAssignee())).isEqualTo(requestAssigneeUser);

        InstallationAccountPayload retrievedAccountPayload = requestPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningApplicationSubmittedRequestActionPayload() {
        final String accountName = "account1";
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        final EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        final String leName = "le1";
        InstallationAccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningApplicationSubmittedRequestActionPayload accountSubmittedPayload = mapper.toInstallationAccountOpeningApplicationSubmittedRequestActionPayload(accountPayload);

        assertThat(accountSubmittedPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD);

        InstallationAccountPayload retrievedAccountPayload = accountSubmittedPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningApplicationRequestTaskPayload() {
        final String accountName = "account";
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.WALES;
        final EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        final String leName = "le";
        InstallationAccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningApplicationRequestTaskPayload
            installationAccountOpeningApplicationRequestTaskPayload =
            mapper.toInstallationAccountOpeningApplicationRequestTaskPayload(accountPayload);

        assertThat(installationAccountOpeningApplicationRequestTaskPayload.getPayloadType())
            .isEqualTo(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD);

        InstallationAccountPayload retrievedAccountPayload = installationAccountOpeningApplicationRequestTaskPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningApprovedRequestActionPayload() {
        final String accountName = "account1";
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        final EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        final String leName = "le1";
        InstallationAccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningApprovedRequestActionPayload accountApprovedPayload =
            mapper.toInstallationAccountOpeningApprovedRequestActionPayload(accountPayload);

        assertThat(accountApprovedPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD);

        InstallationAccountPayload retrievedAccountPayload = accountApprovedPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    private InstallationAccountPayload createAccountPayload(String accountName, CompetentAuthorityEnum ca, EmissionTradingScheme ets, String leName) {
        return InstallationAccountPayload.builder()
            .accountType(AccountType.INSTALLATION)
            .name(accountName)
            .competentAuthority(ca)
            .emissionTradingScheme(ets)
            .commencementDate(LocalDate.of(2020,8,6))
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder()
                .type(LegalEntityType.PARTNERSHIP)
                .name(leName)
                .referenceNumber("09546038")
                .noReferenceNumberReason("noCompaniesRefDetails")
                .address(AddressDTO.builder().build())
                .build())
            .build();
    }
}