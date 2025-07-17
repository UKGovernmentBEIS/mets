package uk.gov.pmrv.api.account.aviation.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.domain.LocationOnShoreState;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.AddressState;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AviationAccountMapperTest {

    private final AviationAccountMapper aviationAccountMapper = Mappers.getMapper(AviationAccountMapper.class);
    private final LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(aviationAccountMapper, "locationMapper", locationMapper);
    }

    @Test
    void toAviationAccount() {
        Long accountId = 10L;
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        Long sopId = 9807L;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022, 12, 11);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        String emitterId = "EM00010";

        AviationAccountCreationDTO accountCreationDTO = AviationAccountCreationDTO.builder()
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .sopId(sopId)
            .commencementDate(commencementDate)
            .build();

        AviationAccount expected = AviationAccount.builder()
            .id(accountId)
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .commencementDate(commencementDate)
            .competentAuthority(competentAuthority)
            .status(AviationAccountStatus.NEW)
            .accountType(AccountType.AVIATION)
            .sopId(sopId)
            .emitterId(emitterId)
            .build();

        AviationAccount actual = aviationAccountMapper.toAviationAccount(accountCreationDTO, competentAuthority, accountId);

        assertEquals(expected, actual);
    }
    
    @Test
    void toAviationAccountInfoDTO() {
    	AviationAccount account = AviationAccount.builder()
    			.accountType(AccountType.AVIATION)
    			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
    			.crcoCode("crcoCode")
    			.emissionTradingScheme(EmissionTradingScheme.CORSIA)
    			.id(1L)
    			.location(LocationOnShoreState.builder()
                        .address(AddressState.builder().line1("line1").state("state").build())
                        .build())
    			.name("name")
    			.reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
    			.status(AviationAccountStatus.LIVE)
    			.build();
    	
    	AviationAccountInfoDTO result = aviationAccountMapper.toAviationAccountInfoDTO(account);
    	
    	assertThat(result).isEqualTo(AviationAccountInfoDTO.builder()
    			.accountType(AccountType.AVIATION)
    			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
    			.crcoCode("crcoCode")
    			.emissionTradingScheme(EmissionTradingScheme.CORSIA)
    			.id(1L)
    			.location(locationMapper.toAccountLocationDTO( account.getLocation() ))
    			.name("name")
    			.reportingStatus(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
    			.status(AviationAccountStatus.LIVE)
    			.build());
    }

    @Test
    void toAviationAccountDTO() {
        Long accountId = 10L;
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022, 12, 11);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        String emitterId = "EM00010";

        AviationAccountReportingStatusHistory reportingStatusEntry1 = AviationAccountReportingStatusHistory.builder()
            .status(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
            .submissionDate(LocalDateTime.now())
            .build();

        AviationAccountReportingStatusHistory reportingStatusEntry2 = AviationAccountReportingStatusHistory.builder()
            .status(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .reason("reason for exempt")
            .submissionDate(LocalDateTime.now())
            .build();

        AviationAccount aviationAccount = AviationAccount.builder()
            .id(accountId)
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .commencementDate(commencementDate)
            .competentAuthority(competentAuthority)
            .status(AviationAccountStatus.NEW)
            .reportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .accountType(AccountType.AVIATION)
            .emitterId(emitterId)
            .reportingStatusHistoryList(List.of(reportingStatusEntry2, reportingStatusEntry1))
            .location(LocationOnShoreState.builder()
                    .address(AddressState.builder().line1("line1").state("state").build())
                    .build())
            .build();

        AviationAccountDTO expected = AviationAccountDTO.builder()
            .id(accountId)
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .commencementDate(commencementDate)
            .competentAuthority(competentAuthority)
            .status(AviationAccountStatus.NEW)
            .accountType(AccountType.AVIATION)
            .reportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .reportingStatusReason(reportingStatusEntry2.getReason())
            .location(LocationOnShoreStateDTO.builder().type(LocationType.ONSHORE_STATE).line1("line1").state("state").build())
            .build();

        AviationAccountDTO actual = aviationAccountMapper.toAviationAccountDTO(aviationAccount);

        assertEquals(expected, actual);
    }

    @Test
    void toAviationAccountDTOIgnoreReportingStatusReason() {
        Long accountId = 10L;
        String accountName = "accountName";
        EmissionTradingScheme emissionTradingScheme = EmissionTradingScheme.CORSIA;
        String crcoCode = "crcoCode";
        LocalDate commencementDate = LocalDate.of(2022, 12, 11);
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        String emitterId = "EM00010";

        AviationAccountReportingStatusHistory reportingStatusEntry = AviationAccountReportingStatusHistory.builder()
            .status(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .reason("reason for exempt")
            .build();

        AviationAccount aviationAccount = AviationAccount.builder()
            .id(accountId)
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .commencementDate(commencementDate)
            .competentAuthority(competentAuthority)
            .status(AviationAccountStatus.NEW)
            .reportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .accountType(AccountType.AVIATION)
            .emitterId(emitterId)
            .reportingStatusHistoryList(List.of(reportingStatusEntry))
            .build();

        AviationAccountDTO expected = AviationAccountDTO.builder()
            .id(accountId)
            .name(accountName)
            .emissionTradingScheme(emissionTradingScheme)
            .crcoCode(crcoCode)
            .commencementDate(commencementDate)
            .competentAuthority(competentAuthority)
            .status(AviationAccountStatus.NEW)
            .accountType(AccountType.AVIATION)
            .reportingStatus(AviationAccountReportingStatus.EXEMPT_COMMERCIAL)
            .build();

        AviationAccountDTO actual = aviationAccountMapper.toAviationAccountDTOIgnoreReportingStatusReason(aviationAccount);

        assertEquals(expected, actual);
    }
}