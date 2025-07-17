package uk.gov.pmrv.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorUserRegistrationMapperTest {

    private OperatorUserRegistrationMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(OperatorUserRegistrationMapper.class);
    }
    
	@Test
	void toUserRepresentation() {
		String fn = "fn";
		String ln = "ln";
		String email = "email";
		OperatorUserRegistrationDTO userRegistrationDTO = createUserRegistrationDTO(fn, ln);
		
		//invoke
		UserRepresentation userRepresentation = mapper.toUserRepresentation(userRegistrationDTO, email);
		
		//assert
		assertThat(userRepresentation.getFirstName()).isEqualTo(fn);
		assertThat(userRepresentation.getLastName()).isEqualTo(ln);
        assertThat(userRepresentation.getCredentials()).isNull();
        assertThat(userRepresentation.getEmail()).isEqualTo(email);
        assertThat(userRepresentation.getUsername()).isEqualTo(email);
        
        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("mobileNumber", null);
        attrs.put("mobileNumberCode", null);
        attrs.put("phoneNumber", List.of("123"));
        attrs.put("phoneNumberCode", List.of("GR"));
        
        assertThat(userRepresentation.getAttributes()).containsExactlyInAnyOrderEntriesOf(attrs);
	}
	
	private OperatorUserRegistrationDTO createUserRegistrationDTO(String firstName, String lastName) {
		return OperatorUserRegistrationDTO.builder()
				.emailToken("dsdsd")
				.firstName(firstName)
				.lastName(lastName)
				.phoneNumber(PhoneNumberDTO.builder().countryCode("GR").number("123").build())
				.build();
	}

}
