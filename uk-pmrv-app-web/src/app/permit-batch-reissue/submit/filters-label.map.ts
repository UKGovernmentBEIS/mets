import { InstallationAccountDTO } from 'pmrv-api';

export const accountStatusLabelMap: Partial<Record<InstallationAccountDTO['status'], string>> = {
  LIVE: 'Live',
  AWAITING_REVOCATION: 'Awaiting revocation',
  AWAITING_SURRENDER: 'Awaiting surrender',
};

export const accountTypeLabelMap: Partial<Record<InstallationAccountDTO['emitterType'], string>> = {
  HSE: 'HSE',
  GHGE: 'GHGE',
  WASTE: 'Waste voluntary',
};

export const accountCategoryLabelMap: Partial<Record<InstallationAccountDTO['installationCategory'], string>> = {
  A_LOW_EMITTER: 'A (low emitter)',
  A: 'A',
  B: 'B',
  C: 'C',
};

export const allocationStatusLabelMap: Partial<Record<'FREE_ALLOCATION' | 'NONFREE_ALLOCATION', string>> = {
  FREE_ALLOCATION: 'Free allocation',
  NONFREE_ALLOCATION: 'Non-free allocation',
};
