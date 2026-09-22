import { AviationAccountCreationDTO } from 'pmrv-api';

type EmissionTradingScheme = AviationAccountCreationDTO['emissionTradingScheme'];

export const FYRO_MIN_YEAR: Partial<Record<EmissionTradingScheme, number>> = {
  UK_ETS_AVIATION: 2021,
  CORSIA: 2019,
};

export function getFyroLabel(scheme: EmissionTradingScheme | null | undefined): string {
  return scheme === 'CORSIA' ? 'First year within the scope of applicability' : 'First year of reporting obligation';
}

export function getFyroRangeErrorMessage(
  scheme: EmissionTradingScheme | null | undefined,
  editModeEnabled: boolean,
): string {
  const minYear = FYRO_MIN_YEAR[scheme] ?? FYRO_MIN_YEAR.UK_ETS_AVIATION;
  const boundaryPhrase = editModeEnabled ? 'later than previously set' : 'later than the current year';

  return `The year must be the same as or after ${minYear} and it cannot be ${boundaryPhrase}`;
}
