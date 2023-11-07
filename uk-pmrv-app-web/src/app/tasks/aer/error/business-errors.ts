import { BusinessError, dashboardLink } from '../../../error/business-error/business-error';

export const notFoundVerificationBodyError = () =>
  new BusinessError(
    'Your named verifier is invalid. Please make sure a verification body has been successfully added to your account.',
  ).withLink(dashboardLink);

export const notCalculationPrimaryPFCError = (taskId: number) =>
  new BusinessError(
    'The PFC source stream is not relevant to the calculation monitoring approach. You must delete the PFC source stream entry.    ',
  ).withLink({
    linkText: 'Return to: Calculation of CO2 emissions',
    link: [`/tasks/${taskId}/aer/submit/calculation-emissions`],
  });
