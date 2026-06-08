package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.domain.AccountDetailsHistory;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsHistoryDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountDetailsHistoryMapper {

    AccountDetailsHistoryDTO toAccountDetailsHistoryDTO(AccountDetailsHistory accountDetailsHistory);

}
