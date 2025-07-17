package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.validation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditValidatorServiceTest {

    @InjectMocks
    private InstallationAuditValidatorService installationAuditValidatorService;

    @Test
    void validateInstallationInspection_inspectionDetailsDateNotNull_throwFormValidationError(){

        InstallationInspection installationInspection =
                InstallationInspection
                .builder()
                .details(InstallationInspectionDetails
                    .builder()
                    .date(LocalDate.now())
                    .officerNames(List.of("officer1","officer2"))
                    .build())
                .build();

        final BusinessException be = assertThrows(BusinessException.class,() -> {
            installationAuditValidatorService.validateInstallationInspection(installationInspection);
        });

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }


    @Test
    void validateInstallationInspection_inspectionDetailsOfficerNamesLessThankTwo_throwFormValidationError(){

       InstallationInspection installationInspection =
            InstallationInspection
            .builder()
            .details(InstallationInspectionDetails
                .builder()
                .date(LocalDate.now())
                .officerNames(List.of("officer1"))
                .build())
            .build();

       final BusinessException be = assertThrows(BusinessException.class,() -> {
            installationAuditValidatorService.validateInstallationInspection(installationInspection);
       });

       assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateInstallationInspection_followupActionsRequired_responseDeadlineEmpty_throwFormValidationError(){


        InstallationInspection installationInspection =
                InstallationInspection
                .builder()
                .followUpActionsRequired(true)
                .details(
                     InstallationInspectionDetails
                        .builder()
                        .officerNames(List.of("officer1","officer1"))
                        .build())
                .build();

       final BusinessException be = assertThrows(BusinessException.class,() -> {
            installationAuditValidatorService.validateInstallationInspection(installationInspection);
       });

       assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validateInstallationInspection_followupActionsNotRequired_responseDeadlineEmpty_doNotThrowError(){

        InstallationInspection installationInspection =
                InstallationInspection
                .builder()
                .followUpActionsRequired(false)
                .details(
                     InstallationInspectionDetails
                        .builder()
                        .officerNames(List.of("officer1","officer1"))
                        .build())
                .build();


        assertThatCode(() -> installationAuditValidatorService.validateInstallationInspection(installationInspection)).doesNotThrowAnyException();

    }

    @Test
    void validateInstallationInspection_followupActionsRequiredNull_responseDeadlineEmpty_throwFormValidationError(){

        InstallationInspection installationInspection =
                InstallationInspection
                .builder()
                .followUpActionsRequired(null)
                .details(
                     InstallationInspectionDetails
                        .builder()
                        .officerNames(List.of("officer1","officer1"))
                        .date(LocalDate.now())
                        .build())
                .build();

       final BusinessException be = assertThrows(BusinessException.class,() -> {
            installationAuditValidatorService.validateInstallationInspection(installationInspection);
       });

       assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void getRequestType(){
        assertThat(installationAuditValidatorService.getRequestType()).isEqualTo(RequestType.INSTALLATION_AUDIT);
    }
}
