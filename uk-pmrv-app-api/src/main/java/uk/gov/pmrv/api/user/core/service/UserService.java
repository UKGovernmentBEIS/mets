package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.transform.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {

	private final AuthService authService;

	private static final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

	public UserDTO getUserByUserId(String userId) {
		return userMapper.toUserDTO(authService.getUserRepresentationById(userId));
	}
}
