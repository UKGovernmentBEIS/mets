package uk.gov.pmrv.api.account.fileattachment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.account.fileattachment.domain.AccountFileAttachment;
import uk.gov.pmrv.api.account.fileattachment.domain.dto.AccountFileAttachmentDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountFileAttachmentMapper {

    AccountFileAttachmentDTO toDto(AccountFileAttachment entity);

    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    AccountFileAttachment createEntity(AccountFileAttachmentDTO req);

    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(AccountFileAttachmentDTO req, @MappingTarget AccountFileAttachment entity);
}
