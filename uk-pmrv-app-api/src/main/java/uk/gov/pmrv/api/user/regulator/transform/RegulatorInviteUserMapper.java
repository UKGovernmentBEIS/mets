package uk.gov.pmrv.api.user.regulator.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RegulatorInviteUserMapper {

    @Mapping(target = "username", source = "email")
    @Mapping(target = "enabled", ignore = true)
    UserRepresentation toUserRepresentation(RegulatorInvitedUserDetailsDTO regulatorInvitedUserDetailsDTO);
    
    @AfterMapping
    default void populateAttributesToUserRepresentation(
            RegulatorInvitedUserDetailsDTO regulatorInvitedUserDetailsDTO, @MappingTarget UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                        regulatorInvitedUserDetailsDTO.getPhoneNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
            regulatorInvitedUserDetailsDTO.getMobileNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(),
                regulatorInvitedUserDetailsDTO.getJobTitle());
    }
}
