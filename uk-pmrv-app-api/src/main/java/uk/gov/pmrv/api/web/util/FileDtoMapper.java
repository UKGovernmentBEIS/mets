package uk.gov.pmrv.api.web.util;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

import java.io.IOException;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FileDtoMapper {

    @Mapping(target = "fileName", source = "originalFilename")
    @Mapping(target = "fileSize", source = "size")
    @Mapping(target = "fileContent", source = "bytes")
    FileDTO toFileDTO(MultipartFile file) throws IOException;

	@AfterMapping
	default void setFileType(@MappingTarget FileDTO fileDTO, MultipartFile file) throws IOException {
		fileDTO.setFileType(MimeTypeUtils.detect(file.getBytes(), file.getOriginalFilename()));
	}
}
