import { EmpBatchReissueState } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.state';

export function isWizardCompleted(batchReissueState: EmpBatchReissueState) {
  return isFiltersWizardStepCompleted(batchReissueState) && isSignatoryWizardStepCompleted(batchReissueState);
}

export function isFiltersWizardStepCompleted(batchReissueState: EmpBatchReissueState) {
  return (
    batchReissueState !== undefined &&
    batchReissueState.reportingStatuses?.length > 0 &&
    batchReissueState.emissionTradingSchemes?.length > 0
  );
}

export function isSignatoryWizardStepCompleted(batchReissueState: EmpBatchReissueState) {
  return batchReissueState !== undefined && !!batchReissueState.signatory;
}
