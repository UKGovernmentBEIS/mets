package uk.gov.pmrv.api.web.orchestrator.uiconfiguration;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.configuration.domain.ui.UIConfigurationPropertiesDTO;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UIPropertiesMapperTest {

	private UIPropertiesMapper cut = Mappers.getMapper(UIPropertiesMapper.class);

    @Test
    void toUIPropertiesDTO() {
    	UIConfigurationPropertiesDTO uiConfigurationPropertiesDTO = UIConfigurationPropertiesDTO.builder()
    			.analytics(Map.of(
    					"key1", "val1",
    					"key2", "val2"
    					))
    			.features(Map.of(
    					"key3", Boolean.TRUE,
    					"key2", Boolean.FALSE
    					))
    			.keycloakServerUrl("keycloakServerUrl")
    			.build();
    	
    	UIPropertiesDTO result = cut.toUIPropertiesDTO(uiConfigurationPropertiesDTO);
    	
    	assertThat(result).isEqualTo(UIPropertiesDTO.builder()
    			.analytics(uiConfigurationPropertiesDTO.getAnalytics())
    			.features(uiConfigurationPropertiesDTO.getFeatures())
    			.keycloakServerUrl(uiConfigurationPropertiesDTO.getKeycloakServerUrl())
    			.build());
    }
    
}
