package uk.gov.pmrv.api.user.regulator.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorCurrentUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;

import java.util.Optional;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RegulatorUserMapper {

    @Mapping(target = "email", source = "userRepresentation.username")
    RegulatorUserDTO toRegulatorUserDTO(UserRepresentation userRepresentation, FileInfoDTO signature);

    @AfterMapping
	default void populateAttributeToRegulatorUserDTO(UserRepresentation userRepresentation,
			@MappingTarget RegulatorUserDTO regulatorUserDTO) {
        if(ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()))
                .ifPresent(list -> regulatorUserDTO.setJobTitle(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> regulatorUserDTO.setPhoneNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> regulatorUserDTO.setMobileNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
    }

    @Mapping(target = "username", source = "email")
    @Mapping(target = "enabled", ignore = true)
	UserRepresentation toUserRepresentation(RegulatorUserDTO regulatorUserDTO);

    @AfterMapping
	default void populateAttributesToUserRepresentation(RegulatorUserDTO regulatorUserDTO,
			@MappingTarget UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(), regulatorUserDTO.getJobTitle());
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(), regulatorUserDTO.getPhoneNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), regulatorUserDTO.getMobileNumber());
    }
    
    RegulatorCurrentUserDTO toRegulatorCurrentUserDTO(RegulatorUserDTO regulatorUserDTO, CompetentAuthorityEnum competentAuthority);

}
