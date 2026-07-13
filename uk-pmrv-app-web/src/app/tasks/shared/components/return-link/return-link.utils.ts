import { TaskTypeToBreadcrumbPipe } from '@shared/pipes/task-type-to-breadcrumb.pipe';

import { RequestTaskDTO } from 'pmrv-api';

export const getTaskName = (taskType: RequestTaskDTO['type']): string => {
  const breadcrumbPipe = new TaskTypeToBreadcrumbPipe();

  switch (taskType) {
    case 'DOAL_APPLICATION_SUBMIT':
      return 'Determination of activity level';
    case 'DOAL_AUTHORITY_RESPONSE':
      return 'Provide UK ETS Authority response for activity level';
    case 'DOAL_APPLICATION_PEER_REVIEW':
      return 'Activity level determination peer review ';
    case 'DOAL_WAIT_FOR_PEER_REVIEW':
      return 'Activity level determination sent to peer reviewer';
    case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT':
      return 'Withholding of allowances';
    case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT':
      return 'Withdraw withholding of allowances notice ';
    case 'NER_APPLICATION_SUBMIT':
      return 'Complete new entrant reserve';

    default:
      return breadcrumbPipe.transform(taskType);
  }
};
