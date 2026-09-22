import { inject, Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AuthStore } from '@core/store';

import { FeeHistoryEntryDTO, FeeRowDTO, SettingsService } from 'pmrv-api';

import { formatFeeHistoryDateTime } from './fee-history-date';
import { getFeeWorkflowLabel, getFeeWorkflowLabelWithFallback } from './fee-workflow-label';
import { FeeHistoryPage, FeeHistoryRecord, FeeRow, UpdateFeeRequest } from './fees.model';

@Injectable({ providedIn: 'root' })
export class FeesService {
  private readonly settingsService = inject(SettingsService);
  private readonly authStore = inject(AuthStore);

  getFees(): Observable<FeeRow[]> {
    return this.settingsService.getFees(this.authStore.currentDomain()).pipe(
      map((rows) =>
        rows
          .map(toFeeRow)
          .filter((row): row is FeeRow => row !== null)
          .sort((a, b) => a.workflow.localeCompare(b.workflow, 'en-GB', { sensitivity: 'base' })),
      ),
    );
  }

  getFee(key: string | null): Observable<FeeRow | undefined> {
    return this.getFees().pipe(map((rows) => rows.find((row) => row.key === key)));
  }

  updateFee(request: UpdateFeeRequest): Observable<void> {
    if (request.feeType == null) {
      return new Observable<void>((subscriber) => subscriber.error(new Error('feeType is required to update a fee')));
    }

    return this.settingsService
      .updateFee(this.authStore.currentDomain(), request.id, request.feeType, {
        amount: String(request.amount),
        effectiveDate: request.effectiveDate,
      })
      .pipe(map((): void => undefined));
  }

  cancelScheduledFeeUpdate(id: number, feeType?: FeeRowDTO['feeType']): Observable<void> {
    if (feeType == null) {
      return new Observable<void>((subscriber) =>
        subscriber.error(new Error('feeType is required to cancel a scheduled fee update')),
      );
    }

    return this.settingsService
      .cancelScheduledFeeUpdate(this.authStore.currentDomain(), id, feeType)
      .pipe(map((): void => undefined));
  }

  /** page is 1-indexed, matching app-pagination's currentPageChange output. */
  getFeeHistory(page: number, size = 30): Observable<FeeHistoryPage> {
    return this.settingsService.getFeeHistory(this.authStore.currentDomain(), page - 1, size).pipe(
      map((response) => ({
        records: (response.history ?? []).map(toFeeHistoryRecord),
        total: response.totalItems ?? 0,
      })),
    );
  }
}

function toFeeRow(dto: FeeRowDTO): FeeRow | null {
  const workflow = getFeeWorkflowLabel(dto.requestType, dto.feeType);

  if (!workflow) {
    return null;
  }

  return {
    key: [dto.requestType, dto.feeType].filter(Boolean).join('-'),
    id: dto.id,
    requestType: dto.requestType,
    feeType: dto.feeType,
    workflow,
    currentAmount: Number(dto.amount),
    scheduledChange:
      dto.scheduledAmount != null && dto.scheduledDate != null
        ? { amount: Number(dto.scheduledAmount), date: dto.scheduledDate }
        : null,
  };
}

function toFeeHistoryRecord(dto: FeeHistoryEntryDTO): FeeHistoryRecord {
  return {
    createdAtDisplay: formatFeeHistoryDateTime(dto.createdAt),
    changedByDisplay: dto.actionType === 'SYSTEM_APPLIED' ? 'System' : (dto.changedBy ?? ''),
    actionType: dto.actionType,
    workflow: getFeeWorkflowLabelWithFallback(dto.requestType, dto.feeType),
    oldAmount: Number(dto.oldAmount ?? 0),
    newAmount: Number(dto.newAmount ?? 0),
    effectiveDate: dto.effectiveDate ?? null,
  } as FeeHistoryRecord;
}
