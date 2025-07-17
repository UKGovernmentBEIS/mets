package uk.gov.pmrv.api.account.aviation.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.service.AccountEmitterIdGenerator;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

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

    AviationAccountInfoDTO toAviationAccountInfoDTO(AviationAccount account);

    @Mapping(target = "reportingStatusReason", expression = "java(account.getReportingStatusHistoryList().get(0).getReason())")
    AviationAccountDTO toAviationAccountDTO(AviationAccount account);

    @Mapping(target = "reportingStatusReason", ignore = true)
    AviationAccountDTO toAviationAccountDTOIgnoreReportingStatusReason(AviationAccount account);

    @AfterMapping
    default void populateAccountEmitterId(@MappingTarget AviationAccount aviationAccount, Long id) {
        aviationAccount.setEmitterId(AccountEmitterIdGenerator.generate(id));
    }
}
