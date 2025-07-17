package uk.gov.pmrv.api.web.orchestrator.uiconfiguration;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.configuration.domain.ui.UIConfigurationPropertiesDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UIPropertiesMapper {

	UIPropertiesDTO toUIPropertiesDTO(UIConfigurationPropertiesDTO uiConfigurationProperties);
	
}
