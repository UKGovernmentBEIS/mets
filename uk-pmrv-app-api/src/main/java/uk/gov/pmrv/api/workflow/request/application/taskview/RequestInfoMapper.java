package uk.gov.pmrv.api.workflow.request.application.taskview;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RequestInfoMapper {

    @Mapping(target = "requestMetadata", source = "metadata")
    @Mapping(target = "paymentCompleted", source = "payload.paymentCompleted")
    @Mapping(target = "paymentAmount", source = "payload.paymentAmount")
    RequestInfoDTO toRequestInfoDTO(Request request);
}
