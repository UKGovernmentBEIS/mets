import { UncorrectedMisstatements } from 'pmrv-api';

export function uncorrectedMisstatementsWizardComplete(uncorrectedMisstatements: UncorrectedMisstatements): boolean {
  return (
    uncorrectedMisstatements &&
    (uncorrectedMisstatements?.areThereUncorrectedMisstatements === false ||
      (uncorrectedMisstatements?.areThereUncorrectedMisstatements &&
        uncorrectedMisstatements?.uncorrectedMisstatements?.length > 0))
  );
}
