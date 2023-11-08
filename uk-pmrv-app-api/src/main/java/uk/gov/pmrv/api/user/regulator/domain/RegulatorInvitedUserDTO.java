package uk.gov.pmrv.api.user.regulator.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatorInvitedUserDTO {

    @Valid
    @JsonUnwrapped
    private RegulatorInvitedUserDetailsDTO userDetails;

    @NotEmpty
    private Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissions;
}
