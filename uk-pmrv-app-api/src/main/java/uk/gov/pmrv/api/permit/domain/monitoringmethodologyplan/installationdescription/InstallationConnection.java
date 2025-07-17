package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(
        expression = "{(#entityType == 'ETS_INSTALLATION' or #entityType == 'NITRIC_ACID_PRODUCTION') ? (#installationId != null and #installationId.trim().length() > 0) : true}",
        message = "permit.monitoringmethodologyplans.digitized.description.connection.invalid_installationId"
)
public class InstallationConnection {

    @NotBlank
    @Size(max = 255)
    private String connectionNo;

    @NotBlank
    @Size(max = 255)
    private String installationOrEntityName;

    @NotNull
    private EntityType entityType;

    @NotNull
    private InstallationConnectionType connectionType;

    @NotNull
    private FlowDirection flowDirection;


    private String installationId;

    @NotBlank
    @Size(max = 255)
    private String contactPersonName;

    @NotBlank
    @Size(max = 255)
    @Email
    private String emailAddress;

    @NotBlank
    @Size(max = 255)
    private String phoneNumber;

}

