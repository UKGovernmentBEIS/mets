import { RequestDetailsDTO } from 'pmrv-api';

export const workflowTypesDomainMap: Record<string, Record<string, RequestDetailsDTO['requestType'][]>> = {
  INSTALLATION: {
    'Account creation': ['INSTALLATION_ACCOUNT_OPENING'],
    'Batch variation': ['PERMIT_REISSUE'],
    'Non-compliance': ['NON_COMPLIANCE'],
    Notification: ['PERMIT_NOTIFICATION'],
    'Permanent cessation': ['PERMANENT_CESSATION'],
    'Permit application': ['PERMIT_ISSUANCE'],
    Revocation: ['PERMIT_REVOCATION'],
    'Return of allowances': ['RETURN_OF_ALLOWANCES'],
    Surrender: ['PERMIT_SURRENDER'],
    Transfer: ['PERMIT_TRANSFER_A', 'PERMIT_TRANSFER_B'],
    Variation: ['PERMIT_VARIATION'],
    'Withholding of allowances': ['WITHHOLDING_OF_ALLOWANCES'],
  },
  AVIATION: {
    'Account closure': ['AVIATION_ACCOUNT_CLOSURE'],
    Application: ['EMP_ISSUANCE_UKETS', 'EMP_ISSUANCE_CORSIA'],
    'Non-compliance': ['AVIATION_NON_COMPLIANCE'],
    Variation: ['EMP_VARIATION_UKETS', 'EMP_VARIATION_CORSIA'],
  },
};
