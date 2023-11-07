import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';

import { VirVerificationData } from 'pmrv-api';

export function matchVerificationItem(id: string, verificationData?: VirVerificationData): VerificationDataItem | null {
  if (verificationData) {
    const filteredGroup = Object.values(verificationData).find((item) => item?.[id] !== undefined);
    return filteredGroup?.[id] ?? null;
  }
  return null;
}
