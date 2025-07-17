package uk.gov.pmrv.api.user.regulator.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegulatorUserUpdateDTO {

    @NotNull(message = "{regulatorUserUpdate.user.notEmpty}")
    @Valid
    private RegulatorUserDTO user;

    @NotEmpty
    private Map<String, RegulatorPermissionLevel> permissions;
}
