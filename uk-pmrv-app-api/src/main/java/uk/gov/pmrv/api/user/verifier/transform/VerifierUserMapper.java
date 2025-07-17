package uk.gov.pmrv.api.user.verifier.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.authorization.AuthorityConstants;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

import java.util.Optional;

/**
 * The Verifier Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface VerifierUserMapper {

    @Mapping(target = "email", source = "username")
    VerifierUserDTO toVerifierUserDTO(UserRepresentation userRepresentation);

    @AfterMapping
	default void populateAttributeToRegulatorUserDTO(UserRepresentation userRepresentation,
			@MappingTarget VerifierUserDTO verifierUserDTO) {
        if(ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }

        /* Set phone number */
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> verifierUserDTO.setPhoneNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        /* Set mobile number */
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> verifierUserDTO.setMobileNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
    }

    @Mapping(target = "username", source = "email")
    @Mapping(target = "enabled", ignore = true)
	UserRepresentation toUserRepresentation(VerifierUserDTO verifierUserDTO);

    @AfterMapping
	default void populateAttributesToUserRepresentation(VerifierUserDTO verifierUserDTO,
			@MappingTarget UserRepresentation userRepresentation) {

        /* Set phone number */
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                verifierUserDTO.getPhoneNumber());

        /* Set mobile number */
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                verifierUserDTO.getMobileNumber());
    }

    @Mapping(target = "username", source = "email")
    @Mapping(target = "enabled", ignore = true)
    UserRepresentation toUserRepresentation(VerifierUserInvitationDTO verifierUserInvitation);

	@AfterMapping
	default void populateAttributesToUserRepresentation(VerifierUserInvitationDTO verifierUserInvitation,
			@MappingTarget UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                verifierUserInvitation.getPhoneNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                verifierUserInvitation.getMobileNumber());
    }

    @Mapping(target = "roleCode", constant = AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE)
	VerifierUserInvitationDTO toVerifierUserInvitationDTO(
			AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO);
}
