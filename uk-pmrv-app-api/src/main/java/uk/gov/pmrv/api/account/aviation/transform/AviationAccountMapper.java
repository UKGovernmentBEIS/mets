package uk.gov.pmrv.api.account.aviation.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.service.AccountEmitterIdGenerator;
import uk.gov.pmrv.api.account.transform.LocationMapper;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Mapper(
        componentModel = "spring",
        uses = {LocationMapper.class},
        config = MapperConfig.class
)
public interface AviationAccountMapper {

    @Mapping(target = "name", source = "aviationAccountCreationDTO.name")
    @Mapping(target = "status", expression = "java(uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus.NEW)")
    @Mapping(target = "accountType", expression = "java(uk.gov.pmrv.api.common.domain.enumeration.AccountType.AVIATION)")
    AviationAccount toAviationAccount(AviationAccountCreationDTO aviationAccountCreationDTO, CompetentAuthorityEnum competentAuthority, Long id);

    @Mapping(target = "reportingStatus", source = "reportingStatusList", qualifiedByName = "mapLatestReportingStatus")
    AviationAccountInfoDTO toAviationAccountInfoDTO(AviationAccount account);

    AviationAccountDTO toAviationAccountDTO(AviationAccount account);

    AviationAccountDTO toAviationAccountDTOIgnoreReportingStatusReason(AviationAccount account);

    @AfterMapping
    default void populateAccountEmitterId(@MappingTarget AviationAccount aviationAccount, Long id) {
        aviationAccount.setEmitterId(AccountEmitterIdGenerator.generate(id));
    }

    @Named("mapLatestReportingStatus")
    default AviationAccountReportingStatusType mapLatestEffectiveYearReportingStatus(List<AviationAccountReportingStatus> reportingStatusList) {
        if (reportingStatusList == null || reportingStatusList.isEmpty()) {
            return null;
        }

        Optional<AviationAccountReportingStatus> accountReportingStatus = reportingStatusList.stream().filter(r-> r.getYear().getValue()== Year.now().getValue()-1).findFirst();
        return accountReportingStatus.map(AviationAccountReportingStatus::getStatus).orElse(null);
    }
}
