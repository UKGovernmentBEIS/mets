export type UrlRequestType =
  | 'rde'
  | 'rfi'
  | 'permit-issuance'
  | 'permit-variation'
  | 'permit-surrender'
  | 'permit-revocation'
  | 'permit-notification'
  | 'permit-transfer'
  | 'dre'
  | 'doal'
  | 'batch-variation'
  | 'aviation'
  | 'non-compliance'
  | 'withholding-allowances'
  | 'return-of-allowances'
  | 'inspection'
  | 'bdr'
  | 'permanent-cessation';

export const urlRequestTypes: UrlRequestType[] = [
  'aviation',
  'rde',
  'rfi',
  'permit-issuance',
  'permit-variation',
  'permit-surrender',
  'permit-revocation',
  'permit-notification',
  'permit-transfer',
  'dre',
  'doal',
  'batch-variation',
  'non-compliance',
  'withholding-allowances',
  'return-of-allowances',
  'inspection',
  'bdr',
  'permanent-cessation',
];
