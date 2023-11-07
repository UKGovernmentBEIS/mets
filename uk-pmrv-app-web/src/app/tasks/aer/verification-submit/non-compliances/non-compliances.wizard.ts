import { UncorrectedNonCompliances } from 'pmrv-api';

export function nonCompliancesWizardComplete(uncorrectedNonCompliances: UncorrectedNonCompliances): boolean {
  return uncorrectedNonCompliances && nonCompliancesComplete(uncorrectedNonCompliances);
}

function nonCompliancesComplete(uncorrectedNonCompliances: UncorrectedNonCompliances): boolean {
  return (
    uncorrectedNonCompliances?.areThereUncorrectedNonCompliances === false ||
    (uncorrectedNonCompliances?.areThereUncorrectedNonCompliances &&
      uncorrectedNonCompliances?.uncorrectedNonCompliances?.length > 0)
  );
}
