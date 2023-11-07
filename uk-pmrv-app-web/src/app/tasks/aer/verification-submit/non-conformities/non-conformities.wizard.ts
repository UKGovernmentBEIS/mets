import { UncorrectedNonConformities } from 'pmrv-api';

export function nonConformitiesWizardComplete(uncorrectedNonConformities: UncorrectedNonConformities): boolean {
  return (
    uncorrectedNonConformities &&
    nonConformitiesPerPlanComplete(uncorrectedNonConformities) &&
    nonConformitiesPreviousYearComplete(uncorrectedNonConformities)
  );
}

function nonConformitiesPerPlanComplete(uncorrectedNonConformities: UncorrectedNonConformities): boolean {
  return (
    uncorrectedNonConformities?.areThereUncorrectedNonConformities === false ||
    (uncorrectedNonConformities?.areThereUncorrectedNonConformities &&
      uncorrectedNonConformities?.uncorrectedNonConformities?.length > 0)
  );
}

function nonConformitiesPreviousYearComplete(uncorrectedNonConformities: UncorrectedNonConformities): boolean {
  return (
    uncorrectedNonConformities?.areTherePriorYearIssues === false ||
    (uncorrectedNonConformities?.areTherePriorYearIssues && uncorrectedNonConformities?.priorYearIssues?.length > 0)
  );
}
