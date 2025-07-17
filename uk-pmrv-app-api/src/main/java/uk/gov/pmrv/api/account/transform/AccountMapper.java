package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountMapper {

	@Mapping(target = "leName", source = "legalEntity.name")
	AccountInfoDTO toAccountInfoDTO(Account account);
}
