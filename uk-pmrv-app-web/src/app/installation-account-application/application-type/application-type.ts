import { InstallationAccountOpeningSubmitApplicationCreateActionPayload } from 'pmrv-api';

export interface ApplicationType {
  applicationType: InstallationAccountOpeningSubmitApplicationCreateActionPayload['applicationType'];
}

export const applicationTypeMap: Record<ApplicationType['applicationType'], string> = {
  NEW_PERMIT: 'I am applying for a new permit',
  TRANSFER: 'I am receiving an existing permit transferred from an existing installation',
};
