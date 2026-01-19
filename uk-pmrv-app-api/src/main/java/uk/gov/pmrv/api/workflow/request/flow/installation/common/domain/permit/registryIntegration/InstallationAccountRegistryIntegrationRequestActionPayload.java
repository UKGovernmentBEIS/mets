package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class InstallationAccountRegistryIntegrationRequestActionPayload extends RequestActionPayload {

    private RegistryIntegrationAccountCreateActivePermit activePermit;
    private RegistryIntegrationOrganizationDetails organizationDetails;

}
