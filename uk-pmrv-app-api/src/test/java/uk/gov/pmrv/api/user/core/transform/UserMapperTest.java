package uk.gov.pmrv.api.user.core.transform;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserInfoDTO_from_UserRepresentation() {
        String userId = "userId";
        String fName = "fName";
        String lName = "lName";
        String userName = "username";
        
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userName);
        userRepresentation.setFirstName(fName);
        userRepresentation.setLastName(lName);
        userRepresentation.setId(userId);
        userRepresentation.setEnabled(Boolean.TRUE);
        
        UserInfoDTO result = userMapper.toUserInfoDTO(userRepresentation);

        assertThat(result).isEqualTo(UserInfoDTO.builder()
            .userId(userId)
            .email(userName)
            .firstName(fName)
            .lastName(lName)
            .enabled(true)
            .build());
    }
    
    @Test
    void toUserInfoDTO_from_UserInfo() {
        String userId = "userId";
        String fName = "fName";
        String lName = "lName";
        UserInfo userInfo = UserInfo.builder().id(userId).firstName(fName).lastName(lName).enabled(true).build();

        UserInfoDTO expectDTO = UserInfoDTO.builder()
            .userId(userId)
            .firstName(fName)
            .lastName(lName)
            .enabled(true)
            .build();

        UserInfoDTO actualDTO = userMapper.toUserInfoDTO(userInfo);

        assertEquals(expectDTO, actualDTO);
    }
    
	@Test
	void toUserDTO() {
		String email = "email";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setEnabled(true);
		userRepresentation.setFirstName("fn");
		userRepresentation.setLastName("ln");

		UserDTO result = userMapper.toUserDTO(userRepresentation);

		assertThat(result).isEqualTo(
				UserDTO.builder().email(email).firstName("fn").lastName("ln").enabled(true).build());
	}
}