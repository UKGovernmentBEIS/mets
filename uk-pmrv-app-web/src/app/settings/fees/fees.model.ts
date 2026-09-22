import { FeeHistoryEntryDTO, FeeRowDTO } from 'pmrv-api';

export interface FeeScheduledChange {
  amount: number;
  date: string;
}

export interface FeeRow {
  key: string;
  id?: number;
  requestType: FeeRowDTO['requestType'];
  feeType?: FeeRowDTO['feeType'];
  workflow: string;
  currentAmount: number;
  scheduledChange: FeeScheduledChange | null;
}

export interface UpdateFeeRequest {
  id: number;
  feeType?: FeeRowDTO['feeType'];
  amount: number;
  effectiveDate: string;
}

export interface FeeHistoryRecord {
  createdAtDisplay: string;
  changedByDisplay: string;
  actionType: FeeHistoryEntryDTO['actionType'];
  workflow: string;
  oldAmount: number;
  newAmount: number;
  effectiveDate: string | null;
}

export interface FeeHistoryPage {
  records: FeeHistoryRecord[];
  total: number;
}

export interface FeeUpdateDraft {
  amount: number;
  effectiveDate: Date;
  changeWhen: 'immediately' | 'future';
}
