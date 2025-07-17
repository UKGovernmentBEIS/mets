package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;


public abstract class InstallationInspectionValidatorService {

    public abstract void validateInstallationInspection(@NotNull @Valid InstallationInspection installationInspection);

    protected abstract RequestType getRequestType();
}
