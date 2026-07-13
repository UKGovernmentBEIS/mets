package uk.gov.pmrv.api.user.core.transform;

import org.mapstruct.Mapper;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.dto.PasswordValidationResponseDTO;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationResponse;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PasswordValidityMapper {

    PasswordValidationResponseDTO toPasswordValidationResponseDTO(KeycloakPasswordValidationResponse response);
}
