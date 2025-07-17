package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.domain.AccountNote;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteDto;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountNoteMapper {

    AccountNoteDto toAccountNoteDTO(AccountNote accountNote);
}
