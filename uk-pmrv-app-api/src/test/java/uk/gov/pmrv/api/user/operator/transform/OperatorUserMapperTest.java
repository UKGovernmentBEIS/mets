package uk.gov.pmrv.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorUserMapperTest {

    private OperatorUserMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(OperatorUserMapper.class);
    }

    @Test
    void toOperatorUserDTO() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
            put(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.MOBILE_NUMBER.getName(), List.of("2101313131"));
        }});

        // Invoke
        OperatorUserDTO operatorUserDTO = mapper.toOperatorUserDTO(userRepresentation);

        // Assert
        assertEquals(userRepresentation.getEmail(), operatorUserDTO.getEmail());
        assertTrue(operatorUserDTO.getEnabled());
        assertEquals(userRepresentation.getFirstName(), operatorUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), operatorUserDTO.getLastName());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getPhoneNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), operatorUserDTO.getPhoneNumber().getNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getMobileNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), operatorUserDTO.getMobileNumber().getNumber());
    }

    @Test
    void toOperatorUserDTO_no_mobile() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
        }});

        // Invoke
        OperatorUserDTO operatorUserDTO = mapper.toOperatorUserDTO(userRepresentation);

        // Assert
        assertEquals(userRepresentation.getEmail(), operatorUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), operatorUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), operatorUserDTO.getLastName());
        assertThat(userRepresentation.isEnabled()).isNull();
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getPhoneNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), operatorUserDTO.getPhoneNumber().getNumber());
        assertNull(operatorUserDTO.getMobileNumber().getCountryCode());
        assertNull(operatorUserDTO.getMobileNumber().getNumber());
    }

    @Test
    void toUserRepresentation() {
        OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
                .email("email")
                .enabled(true)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .mobileNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(operatorUserDTO);

        // Assert
        assertEquals("email", userRepresentation.getEmail());
        assertEquals(operatorUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(operatorUserDTO.getLastName(), userRepresentation.getLastName());
        assertThat(userRepresentation.isEnabled()).isNull();
        assertEquals(operatorUserDTO.getPhoneNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getPhoneNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(operatorUserDTO.getMobileNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getMobileNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toUserRepresentation_no_mobile() {
        OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
                .email("email")
                .enabled(true)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(operatorUserDTO);

        // Assert
        assertEquals("email", userRepresentation.getEmail());
        assertEquals(operatorUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(operatorUserDTO.getLastName(), userRepresentation.getLastName());
        assertThat(userRepresentation.isEnabled()).isNull();
        assertEquals(operatorUserDTO.getPhoneNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getPhoneNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).isEmpty());
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).isEmpty());
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
