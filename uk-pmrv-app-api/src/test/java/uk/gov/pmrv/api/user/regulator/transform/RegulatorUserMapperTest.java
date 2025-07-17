package uk.gov.pmrv.api.user.regulator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorCurrentUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegulatorUserMapperTest {

    private RegulatorUserMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RegulatorUserMapper.class);
    }

    @Test
    void toRegulatorUserDTO() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
            put(KeycloakUserAttributes.MOBILE_NUMBER.getName(), List.of("699999999"));
            put(KeycloakUserAttributes.JOB_TITLE.getName(), List.of("jobTitle"));
        }});
        
        String signatureUuid = UUID.randomUUID().toString();
        FileInfoDTO signature = FileInfoDTO.builder()
                .name("sign").uuid(signatureUuid)
                .build();

        // Invoke
        RegulatorUserDTO regulatorUserDTO = mapper.toRegulatorUserDTO(userRepresentation, signature);

        // Assert
        assertEquals(userRepresentation.getEmail(), regulatorUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), regulatorUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), regulatorUserDTO.getLastName());
        assertTrue(regulatorUserDTO.getEnabled());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), regulatorUserDTO.getPhoneNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), regulatorUserDTO.getMobileNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()).get(0), regulatorUserDTO.getJobTitle());
        assertThat(regulatorUserDTO.getSignature().getName()).isEqualTo("sign");
        assertThat(regulatorUserDTO.getSignature().getUuid()).isEqualTo(signatureUuid);
    }

    @Test
    void toUserRepresentation() {
        String email = "email@email";

        RegulatorUserDTO regulatorUserDTO = RegulatorUserDTO.builder()
                .email(email)
                .enabled(true)
                .firstName("firstName")
                .lastName("lastName")
                .jobTitle("jobTitle")
                .phoneNumber("2101313131")
                .mobileNumber("699999999")
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(regulatorUserDTO);

        // Assert
        assertEquals(email, userRepresentation.getEmail());
        assertEquals(email, userRepresentation.getUsername());
        assertEquals(regulatorUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(regulatorUserDTO.getLastName(), userRepresentation.getLastName());
        assertThat(userRepresentation.isEnabled()).isNull();
        assertEquals(regulatorUserDTO.getJobTitle(), userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()).get(0));
        assertEquals(regulatorUserDTO.getPhoneNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(regulatorUserDTO.getMobileNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toRegulatorCurrentUserDTO() {
        RegulatorUserDTO regulatorUserDTO = RegulatorUserDTO.builder()
        		.firstName("fn")
        		.lastName("ln")
        		.jobTitle("jobTitle")
        		.email("email@email").build();

        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

        // Invoke
        RegulatorCurrentUserDTO result = mapper.toRegulatorCurrentUserDTO(regulatorUserDTO, ca);

        // Assert
        assertEquals(regulatorUserDTO.getEmail(), result.getEmail());
        assertEquals(regulatorUserDTO.getFirstName(), result.getFirstName());
        assertEquals(regulatorUserDTO.getLastName(), result.getLastName());
        assertEquals(ca, result.getCompetentAuthority());
    }

    private UserRepresentation buildUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("username");
        userRepresentation.setId("userId");
        userRepresentation.setUsername("username");
        userRepresentation.setFirstName("FirstName");
        userRepresentation.setLastName("LastName");

        return userRepresentation;
    }
}
