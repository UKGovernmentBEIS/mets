import { OperatorImprovementResponse, VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export function submitWizardComplete(payload: VirApplicationSubmitRequestTaskPayload): boolean {
  const verificationData = payload?.verificationData;
  const flattenedReferences = [
    ...Object.keys(verificationData?.uncorrectedNonConformities || {}),
    ...Object.keys(verificationData?.recommendedImprovements || {}),
    ...Object.keys(verificationData?.priorYearIssues || {}),
  ];

  return flattenedReferences.every((reference) => payload?.virSectionsCompleted?.[reference] === true);
}

export function operatorImprovementResponseComplete(
  reference: string,
  operatorImprovementResponses?: VirApplicationSubmitRequestTaskPayload['operatorImprovementResponses'],
): boolean {
  return operatorImprovementResponses
    ? addressedComplete(operatorImprovementResponses?.[reference]) &&
        evidenceComplete(operatorImprovementResponses?.[reference])
    : false;
}

function addressedComplete(operatorImprovementResponse: OperatorImprovementResponse): boolean {
  return (
    (operatorImprovementResponse?.isAddressed === true &&
      !!operatorImprovementResponse?.addressedDescription &&
      !!operatorImprovementResponse?.addressedDate) ||
    (operatorImprovementResponse?.isAddressed === false && !!operatorImprovementResponse?.addressedDescription)
  );
}

function evidenceComplete(operatorImprovementResponse: OperatorImprovementResponse): boolean {
  return (
    operatorImprovementResponse?.uploadEvidence === false ||
    (operatorImprovementResponse?.uploadEvidence && operatorImprovementResponse?.files?.length > 0)
  );
}
