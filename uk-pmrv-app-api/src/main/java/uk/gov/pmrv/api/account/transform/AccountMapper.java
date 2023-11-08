package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountMapper {

	@Mapping(target = "leName", source = "legalEntity.name")
	AccountInfoDTO toAccountInfoDTO(Account account);
}
