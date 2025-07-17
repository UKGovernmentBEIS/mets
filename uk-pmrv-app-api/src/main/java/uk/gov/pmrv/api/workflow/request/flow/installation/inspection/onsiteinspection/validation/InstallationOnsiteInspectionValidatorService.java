package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.validation;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.validation.InstallationInspectionValidatorService;

@Service
@Validated
public class InstallationOnsiteInspectionValidatorService extends InstallationInspectionValidatorService {


    @Override
    public void validateInstallationInspection(@NotNull @Valid InstallationInspection installationInspection) {

        if (ObjectUtils.isEmpty(installationInspection.getDetails().getDate())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        if (installationInspection.getDetails().getOfficerNames().isEmpty()) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }


        if (ObjectUtils.isEmpty(installationInspection.getFollowUpActionsRequired()) ||
                installationInspection.getFollowUpActionsRequired()) {
            if (ObjectUtils.isEmpty(installationInspection.getResponseDeadline())) {
                throw new BusinessException(ErrorCode.FORM_VALIDATION);
            }
        }
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.INSTALLATION_ONSITE_INSPECTION;
    }
}
