package uk.gov.pmrv.api.user.operator.transform;

import jakarta.validation.Valid;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface OperatorUserRegistrationMapper {
	
	@Mapping(source = "email", target = "username")
    @Mapping(source = "email", target = "email")
	UserRepresentation toUserRepresentation(@Valid OperatorUserRegistrationDTO operatorUserRegistrationDTO,
			String email);

	@AfterMapping
	default void populateAttributesToUserRepresentation(OperatorUserRegistrationDTO operatorUserRegistrationDTO,
			@MappingTarget UserRepresentation userRepresentation) {
    	Map<String, List<String>> attributes = new HashMap<>();

        attributes.put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
            asList(operatorUserRegistrationDTO.getPhoneNumber().getCountryCode()));
        attributes.put(KeycloakUserAttributes.PHONE_NUMBER.getName(),
            asList(operatorUserRegistrationDTO.getPhoneNumber().getNumber()));
        attributes.put(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
            (Objects.isNull(operatorUserRegistrationDTO.getMobileNumber()) ||
                ObjectUtils.isEmpty(operatorUserRegistrationDTO.getMobileNumber().getCountryCode())) ? null
                : asList(operatorUserRegistrationDTO.getMobileNumber().getCountryCode()));
        attributes.put(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
            (Objects.isNull(operatorUserRegistrationDTO.getMobileNumber()) ||
                ObjectUtils.isEmpty(operatorUserRegistrationDTO.getMobileNumber().getNumber())) ? null
                : asList(operatorUserRegistrationDTO.getMobileNumber().getNumber()));

        if(userRepresentation.getAttributes() == null) {
        	userRepresentation.setAttributes(new HashMap<>());
        }
        
        userRepresentation.getAttributes().putAll(attributes);
    }

    private <T> List<T> asList(T value){
    	return List.of(value);
    }
}
