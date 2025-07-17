import { RequestActionDTO } from 'pmrv-api';

import { OperatorSubmittedComponent } from './aer/submitted/workflow-report/operator-submitted/operator-submitted.component';
import { OperatorToVerifierComponent } from './aer/submitted/workflow-report/operator-to-verifier/operator-to-verifier.component';
import { VerifierSubmittedComponent } from './aer/submitted/workflow-report/verifier-submitted/verifier-submitted.component';

export const reportableAerRequestActionTypes: RequestActionDTO['type'][] = [
  'AER_APPLICATION_SUBMITTED',
  'AER_APPLICATION_AMENDS_SUBMITTED',
  'AER_APPLICATION_VERIFICATION_SUBMITTED',
  'AER_APPLICATION_SENT_TO_VERIFIER',
  'AER_APPLICATION_AMENDS_SENT_TO_VERIFIER',
];

export const reportComponentRequestActionTypes: {
  reportComponent: any;
  requestActionTypes: RequestActionDTO['type'][];
}[] = [
  {
    reportComponent: OperatorSubmittedComponent,
    requestActionTypes: ['AER_APPLICATION_SUBMITTED', 'AER_APPLICATION_AMENDS_SUBMITTED'],
  },
  { reportComponent: VerifierSubmittedComponent, requestActionTypes: ['AER_APPLICATION_VERIFICATION_SUBMITTED'] },
  {
    reportComponent: OperatorToVerifierComponent,
    requestActionTypes: ['AER_APPLICATION_SENT_TO_VERIFIER', 'AER_APPLICATION_AMENDS_SENT_TO_VERIFIER'],
  },
];
