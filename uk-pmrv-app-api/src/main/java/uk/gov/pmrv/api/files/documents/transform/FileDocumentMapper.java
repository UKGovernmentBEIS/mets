package uk.gov.pmrv.api.files.documents.transform;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.domain.FileDocument;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = UUID.class)
public interface FileDocumentMapper {

    FileDTO toFileDTO(FileDocument fileDocument);
    
    @Mapping(target = "name", source = "fileName")
    FileInfoDTO toFileInfoDTO(FileDocument fileDocument);
}
