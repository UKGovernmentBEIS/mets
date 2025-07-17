package uk.gov.pmrv.api.allowance.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#year == null) || (T(java.time.Year).parse(#year).getValue() >= 2021 && T(java.time.Year).parse(#year).getValue() <= 2030)}",
    message = "allowance.activity.level.year.range")
@SpELExpression(expression = "{(#changeType eq 'OTHER') == (#otherChangeTypeName != null)}", 
    message = "allowance.activity.level.change.type.other")
public class ActivityLevel {

    @NotNull
    private Year year;

    @NotNull
    private SubInstallationName subInstallationName;

    @NotNull
    private ChangeType changeType;

    @Size(max = 255)
    private String otherChangeTypeName;

    @Size(max = 255)
    private String changedActivityLevel;

    @NotBlank
    @Size(max = 10000)
    private String comments;
}
