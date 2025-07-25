package uk.gov.pmrv.api.workflow.request.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;

/**
 * The Request Mapper.
 * Note: the dtos returned are not deep copies. Thus, modifications inside the properties of the dto will also 
 * modify the entity.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RequestActionMapper {
    
    @Mapping(target = "id", source = "requestAction.id")
    RequestActionInfoDTO toRequestActionInfoDTO(RequestAction requestAction);

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requestAccountId", source = "request.accountId")
    @Mapping(target = "requestType", source = "request.type")
    @Mapping(target = "competentAuthority", source = "request.competentAuthority")
    RequestActionDTO toRequestActionDTO(RequestAction requestAction);

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requestAccountId", source = "request.accountId")
    @Mapping(target = "requestType", source = "request.type")
    @Mapping(target = "competentAuthority", source = "request.competentAuthority")
    @Mapping(target = "payload", ignore = true)
    RequestActionDTO toRequestActionDTOIgnorePayload(RequestAction requestAction);
}
