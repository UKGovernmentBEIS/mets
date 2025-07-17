import { PermitBatchReissueState } from './store/permit-batch-reissue.state';

export function isWizardCompleted(batchReissueState: PermitBatchReissueState) {
  return isFiltersWizardStepCompleted(batchReissueState) && isSignatoryWizardStepCompleted(batchReissueState);
}

export function isFiltersWizardStepCompleted(batchReissueState: PermitBatchReissueState) {
  return (
    batchReissueState !== undefined &&
    batchReissueState.accountStatuses?.length > 0 &&
    batchReissueState.emitterTypes?.length > 0 &&
    (((batchReissueState.emitterTypes?.includes('GHGE') || batchReissueState.emitterTypes?.includes('WASTE')) &&
      batchReissueState?.installationCategories?.length > 0) ||
      (!batchReissueState.emitterTypes?.includes('GHGE') &&
        !batchReissueState.emitterTypes?.includes('WASTE') &&
        (!batchReissueState?.installationCategories || batchReissueState?.installationCategories.length === 0)))
  );
}

export function isSignatoryWizardStepCompleted(batchReissueState: PermitBatchReissueState) {
  return batchReissueState !== undefined && !!batchReissueState.signatory;
}
