package uk.gov.pmrv.api.user.regulator.domain;

import java.util.Map;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel;

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
    private Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissions;
}
