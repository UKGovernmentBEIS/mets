package uk.gov.pmrv.api.user.regulator.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatorInvitedUserDTO {

    @Valid
    @JsonUnwrapped
    private RegulatorInvitedUserDetailsDTO userDetails;

    @NotEmpty
    private Map<String, RegulatorPermissionLevel> permissions;
}
