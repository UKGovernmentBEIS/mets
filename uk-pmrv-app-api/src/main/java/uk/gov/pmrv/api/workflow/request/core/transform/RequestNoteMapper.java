package uk.gov.pmrv.api.workflow.request.core.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestNote;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestNoteDto;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RequestNoteMapper {

    RequestNoteDto toRequestNoteDTO(RequestNote requestNote);
}
