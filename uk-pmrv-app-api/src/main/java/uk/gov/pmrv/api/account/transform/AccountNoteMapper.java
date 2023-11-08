package uk.gov.pmrv.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.account.domain.AccountNote;
import uk.gov.pmrv.api.account.domain.dto.AccountNoteDto;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountNoteMapper {

    AccountNoteDto toAccountNoteDTO(AccountNote accountNote);
}
