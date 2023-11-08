package uk.gov.pmrv.api.files.notes.transform;

import java.io.IOException;
import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.notes.domain.FileNote;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FileNoteMapper {

    FileNote toFileNote(FileDTO fileDTO) throws IOException;

}
