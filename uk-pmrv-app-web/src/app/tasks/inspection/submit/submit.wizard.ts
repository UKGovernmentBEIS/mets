import { InstallationInspection } from 'pmrv-api';

export const isInstallationInspectionFollowUpSubmitCompleted = (inspection: InstallationInspection): boolean => {
  return (
    (inspection?.followUpActions?.length > 0 &&
      !!inspection?.responseDeadline &&
      inspection?.followUpActionsRequired) ||
    inspection?.followUpActionsRequired === false
  );
};

export const responseDeadlineValid = (responseDeadline: InstallationInspection['responseDeadline']): boolean => {
  return new Date(responseDeadline) > new Date();
};

export const isInstallationInspectionDetailsSubmitCompleted = (inspection: InstallationInspection): boolean => {
  return inspection?.details?.officerNames?.length > 0 && Object.keys(inspection?.details).length >= 1;
};

export const onSiteInspectionDateValid = (onSiteInspectionDate: InstallationInspection['details']['date']): boolean => {
  return new Date(onSiteInspectionDate) < new Date();
};
