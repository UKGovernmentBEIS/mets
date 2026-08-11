import { inject, Injectable } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AuthStore } from '@core/store';

import { FeeRowDTO, SettingsService } from 'pmrv-api';

import { getFeeWorkflowLabel } from './fee-workflow-label';
import { FeeRow } from './fees.model';

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
}

function toFeeRow(dto: FeeRowDTO): FeeRow | null {
  const workflow = getFeeWorkflowLabel(dto.requestType, dto.feeType);

  if (!workflow) {
    return null;
  }

  return {
    key: [dto.requestType, dto.feeType].filter(Boolean).join('-'),
    workflow,
    currentAmount: Number(dto.amount),
    scheduledChange:
      dto.scheduledAmount != null && dto.scheduledDate != null
        ? { amount: Number(dto.scheduledAmount), date: dto.scheduledDate }
        : null,
  };
}
