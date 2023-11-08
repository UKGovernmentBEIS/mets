package uk.gov.pmrv.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorUserRegistrationMapperTest {

    private OperatorUserRegistrationMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(OperatorUserRegistrationMapper.class);
    }
    
	@Test
	void toUserRepresentation_when_operatorUserRegistrationWithCredentialsDTO_with_address() {
		String fn = "fn";
		String ln = "ln";
		String password = "password";
		OperatorUserRegistrationWithCredentialsDTO
            userRegistrationDTO = createUserRegistrationWithCredentialsDTO(fn, ln, password);
		
		//invoke
		UserRepresentation userRepresentation = 
				mapper.toUserRepresentation(userRegistrationDTO, "email");
		
		//assert
		assertThat(userRepresentation.getFirstName()).isEqualTo(fn);
		assertThat(userRepresentation.getLastName()).isEqualTo(ln);
        assertThat(userRepresentation.getCredentials()).hasSize(1);
        assertThat(userRepresentation.getCredentials()).isSubsetOf(getPasswordCredentials(password));
	}
	
	@Test
	void toUserRepresentation_when_operatorUserRegistrationWithCredentialsDTO_no_address() {
		String fn = "fn";
		String ln = "ln";
        String password = "password";
		OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO = createUserRegistrationWithCredentialsDTO(fn, ln, password);
		
		//invoke
		UserRepresentation userRepresentation = 
				mapper.toUserRepresentation(userRegistrationDTO, "email");
		
		//assert
		assertThat(userRepresentation.getFirstName()).isEqualTo(fn);
		assertThat(userRepresentation.getLastName()).isEqualTo(ln);
		assertThat(userRepresentation.getCredentials()).hasSize(1);
		assertThat(userRepresentation.getCredentials()).isSubsetOf(getPasswordCredentials(password));
	}

    @Test
    void toUserRepresentation_when_operatorUserRegistrationDTO() {
        String firstName = "firstName";
        String lastName = "lastName";
        String userId = "userId";
        String userEmail = "userEmail";
        OperatorUserRegistrationDTO userRegistrationDTO = OperatorUserRegistrationDTO.builder()
            .emailToken("emailToken")
            .firstName(firstName)
            .lastName(lastName)
            .phoneNumber(PhoneNumberDTO.builder().countryCode("GR").number("123").build())
            .termsVersion((short) 1)
            .build();

        //invoke
        UserRepresentation userRepresentation =
            mapper.toUserRepresentation(userRegistrationDTO, userEmail, userId);

        //assert
        assertThat(userRepresentation.getFirstName()).isEqualTo(firstName);
        assertThat(userRepresentation.getLastName()).isEqualTo(lastName);
        assertThat(userRepresentation.getCredentials()).isNull();
    }
	
	private OperatorUserRegistrationWithCredentialsDTO createUserRegistrationWithCredentialsDTO(String firstName,
                                                                                                String lastName,
                                                                                                String password) {
		return OperatorUserRegistrationWithCredentialsDTO.builder()
				.password(password)
				.emailToken("dsdsd")
				.firstName(firstName)
				.lastName(lastName)
				.phoneNumber(PhoneNumberDTO.builder().countryCode("GR").number("123").build())
				.termsVersion(Short.valueOf((short)1))
				.build();
	}

	private List<CredentialRepresentation> getPasswordCredentials(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        return List.of(credentialRepresentation);
    }
}
