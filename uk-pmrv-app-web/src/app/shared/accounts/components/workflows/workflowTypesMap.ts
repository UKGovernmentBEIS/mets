import { RequestDetailsDTO } from 'pmrv-api';

export const workflowTypesDomainMap: Record<string, Record<string, RequestDetailsDTO['requestType'][]>> = {
  INSTALLATION: {
    'Permit application': ['PERMIT_ISSUANCE'],
    Notification: ['PERMIT_NOTIFICATION'],
    Variation: ['PERMIT_VARIATION'],
    Revocation: ['PERMIT_REVOCATION'],
    Surrender: ['PERMIT_SURRENDER'],
    Transfer: ['PERMIT_TRANSFER_A', 'PERMIT_TRANSFER_B'],
    'Account creation': ['INSTALLATION_ACCOUNT_OPENING'],
    'Non-compliance': ['NON_COMPLIANCE'],
    'Batch variation': ['PERMIT_REISSUE'],
    'Withholding of allowances': ['WITHHOLDING_OF_ALLOWANCES'],
    'Return of allowances': ['RETURN_OF_ALLOWANCES'],
  },
  AVIATION: {
    Application: ['EMP_ISSUANCE_UKETS', 'EMP_ISSUANCE_CORSIA'],
    Variation: ['EMP_VARIATION_UKETS', 'EMP_VARIATION_CORSIA'],
    'Account closure': ['AVIATION_ACCOUNT_CLOSURE'],
    'Non-compliance': ['AVIATION_NON_COMPLIANCE'],
  },
};
