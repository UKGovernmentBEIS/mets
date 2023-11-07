import { InstallationAccountDTO } from 'pmrv-api';

export const accountStatusLabelMap: Partial<Record<InstallationAccountDTO['status'], string>> = {
  LIVE: 'Live',
  AWAITING_REVOCATION: 'Awaiting revocation',
  AWAITING_SURRENDER: 'Awaiting surrender',
};

export const accountTypeLabelMap: Partial<Record<InstallationAccountDTO['emitterType'], string>> = {
  HSE: 'HSE',
  GHGE: 'GHGE',
};

export const accountCategoryLabelMap: Partial<Record<InstallationAccountDTO['installationCategory'], string>> = {
  A_LOW_EMITTER: 'A (low emitter)',
  A: 'A',
  B: 'B',
  C: 'C',
};
