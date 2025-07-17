package uk.gov.pmrv.api.user.operator.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

import java.util.Optional;

@Mapper(componentModel = "spring", config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperatorUserMapper {

    @Mapping(target = "email", source = "username")
    OperatorUserDTO toOperatorUserDTO(UserRepresentation userRepresentation);

    @AfterMapping
    default void populateAttributeToOperatorUserDTO(UserRepresentation userRepresentation, @MappingTarget OperatorUserDTO operatorUserDTO) {
        if(ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }

        /* Set phone number */
        PhoneNumberDTO phoneNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()))
                .ifPresent(list -> phoneNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> phoneNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        operatorUserDTO.setPhoneNumber(phoneNumber);

        /* Set Mobile number */
        PhoneNumberDTO mobileNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()))
                .ifPresent(list -> mobileNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> mobileNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        operatorUserDTO.setMobileNumber(mobileNumber);
    }

    @Mapping(target = "username", source = "operatorUserDTO.email")
    @Mapping(target = "email", source = "operatorUserDTO.email")
    @Mapping(target = "firstName", source = "operatorUserDTO.firstName")
    @Mapping(target = "lastName", source = "operatorUserDTO.lastName")
    @Mapping(target = "enabled", ignore = true)
	UserRepresentation toUserRepresentation(OperatorUserDTO operatorUserDTO);

    @AfterMapping
    default void populateAttributesToUserRepresentation(OperatorUserDTO operatorUserDTO, @MappingTarget UserRepresentation userRepresentation) {

        // Set phone numbers
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
                operatorUserDTO.getPhoneNumber().getCountryCode());
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                operatorUserDTO.getPhoneNumber().getNumber());

        Optional.ofNullable(operatorUserDTO.getMobileNumber()).ifPresentOrElse(phoneNumberDTO -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
                    phoneNumberDTO.getCountryCode());
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                    phoneNumberDTO.getNumber());
        }, () -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), null);
        });
    }

    @Mapping(target = "username", source = "operatorUserInvitation.email")
    @Mapping(target = "email", source = "operatorUserInvitation.email")
    @Mapping(target = "firstName", source = "operatorUserInvitation.firstName")
    @Mapping(target = "lastName", source = "operatorUserInvitation.lastName")
    @Mapping(target = "enabled", ignore = true)
    UserRepresentation toUserRepresentation(OperatorUserInvitationDTO operatorUserInvitation);
}
