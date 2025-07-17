package uk.gov.pmrv.api.user.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@SuperBuilder
public class UserDTO {

	/** The user email. */
	@Email(message = "{userAccount.email.typeMismatch}")
	@Size(max = 255, message = "{userAccount.email.typeMismatch}")
	@NotBlank(message = "{userAccount.email.notEmpty}")
	private String email;

	/** The first name. */
	@NotBlank(message = "{userAccount.firstName.notEmpty}")
	@Size(max = 255, message = "{userAccount.firstName.typeMismatch}")
	private String firstName;

	/** The last name. */
	@NotBlank(message = "{userAccount.lastName.notEmpty}")
	@Size(max = 255, message = "{userAccount.lastName.typeMismatch}")
	private String lastName;
	
	/**
	 * Annotated with JsonIgnore because enabled should never served/posted to/by the web layer.
	 */
	@JsonIgnore
    private Boolean enabled;
	
	@JsonIgnore
	public String getFullName() {
	    return firstName + " " + lastName;
	}
	
}
