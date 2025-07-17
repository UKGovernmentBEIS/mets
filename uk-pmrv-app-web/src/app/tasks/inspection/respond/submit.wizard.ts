import { InstallationInspectionOperatorRespondSaveRequestTaskActionPayload } from 'pmrv-api';

export function isInstallationInspectionRespondCompleted(
  responses: InstallationInspectionOperatorRespondSaveRequestTaskActionPayload['followupActionsResponses'],
  statusKey: string,
): boolean {
  const action = responses[statusKey] || ({} as any);

  return (
    (action.completed && action.explanation && !!action.completionDate) || (!action.completed && !!action.explanation)
  );
}
