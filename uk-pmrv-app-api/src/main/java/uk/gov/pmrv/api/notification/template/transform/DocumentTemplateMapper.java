package uk.gov.pmrv.api.notification.template.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;

@Mapper(componentModel = "spring", uses = TemplateInfoMapper.class, config = MapperConfig.class)
public interface DocumentTemplateMapper {

    @Mapping(target = "name", source="documentTemplate.name")
    @Mapping(target = "fileUuid", source = "fileInfoDTO.uuid")
    @Mapping(target = "filename", source = "fileInfoDTO.name")
    DocumentTemplateDTO toDocumentTemplateDTO(DocumentTemplate documentTemplate, FileInfoDTO fileInfoDTO);
}
