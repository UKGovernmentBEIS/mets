export interface FeeScheduledChange {
  amount: number;
  date: string;
}

export interface FeeRow {
  key: string;
  workflow: string;
  currentAmount: number;
  scheduledChange: FeeScheduledChange | null;
}
