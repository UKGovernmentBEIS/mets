package uk.gov.pmrv.api.user.verifier.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.netz.api.authorization.AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE;

class VerifierUserMapperTest {

    private VerifierUserMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(VerifierUserMapper.class);
    }

    @Test
    void toVerifierUserDTO() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
            put(KeycloakUserAttributes.MOBILE_NUMBER.getName(), List.of("2101313132"));
        }});

        // Invoke
        VerifierUserDTO verifierUserDTO = mapper.toVerifierUserDTO(userRepresentation);

        // Assert
        assertEquals(userRepresentation.getUsername(), verifierUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), verifierUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), verifierUserDTO.getLastName());
        assertTrue(verifierUserDTO.getEnabled());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), verifierUserDTO.getPhoneNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), verifierUserDTO.getMobileNumber());
    }

    @Test
    void toUserRepresentation() {
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder()
                .email("email@email")
                .enabled(true)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("2101313131")
                .mobileNumber("2101313132")
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(verifierUserDTO);

        // Assert
        assertEquals(verifierUserDTO.getEmail(), userRepresentation.getUsername());
        assertEquals(verifierUserDTO.getEmail(), userRepresentation.getEmail());
        assertEquals(verifierUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(verifierUserDTO.getLastName(), userRepresentation.getLastName());
        assertThat(userRepresentation.isEnabled()).isNull();
        assertEquals(verifierUserDTO.getPhoneNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(verifierUserDTO.getMobileNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toUserRepresentation_UserInvitation() {
    	String email = "email@email";
        VerifierUserInvitationDTO verifierUserInvitationDTO = VerifierUserInvitationDTO.builder()
                .roleCode("roleCode")
                .email(email)
                .enabled(true)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("2101313131")
                .mobileNumber("2101313132")
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(verifierUserInvitationDTO);

        // Assert
        assertEquals(email, userRepresentation.getUsername());
        assertEquals(email, userRepresentation.getEmail());
        assertThat(userRepresentation.isEnabled()).isNull();
        assertEquals(verifierUserInvitationDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(verifierUserInvitationDTO.getLastName(), userRepresentation.getLastName());
        assertEquals(verifierUserInvitationDTO.getPhoneNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(verifierUserInvitationDTO.getMobileNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toVerifierUserInvitationDTO() {
        AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO = AdminVerifierUserInvitationDTO.builder()
            .email("email")
            .firstName("firstName")
            .lastName("lastName")
            .phoneNumber("2101313131")
            .mobileNumber("2101313132")
            .build();

        VerifierUserInvitationDTO verifierUserInvitationDTO =
            mapper.toVerifierUserInvitationDTO(adminVerifierUserInvitationDTO);

        assertNotNull(verifierUserInvitationDTO);
        assertEquals(adminVerifierUserInvitationDTO.getEmail(), verifierUserInvitationDTO.getEmail());
        assertEquals(adminVerifierUserInvitationDTO.getFirstName(), verifierUserInvitationDTO.getFirstName());
        assertEquals(adminVerifierUserInvitationDTO.getLastName(), verifierUserInvitationDTO.getLastName());
        assertEquals(adminVerifierUserInvitationDTO.getMobileNumber(), verifierUserInvitationDTO.getMobileNumber());
        assertEquals(adminVerifierUserInvitationDTO.getPhoneNumber(), verifierUserInvitationDTO.getPhoneNumber());
        assertEquals(VERIFIER_ADMIN_ROLE_CODE, verifierUserInvitationDTO.getRoleCode());

    }

    private UserRepresentation buildUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("email@email");
        userRepresentation.setId("userId");
        userRepresentation.setUsername("username");
        userRepresentation.setFirstName("FirstName");
        userRepresentation.setLastName("LastName");

        return userRepresentation;
    }
}
