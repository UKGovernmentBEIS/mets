import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map } from 'rxjs';

import { NonComplianceFinalDeterminationRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NonComplianceService } from '../../core/non-compliance.service';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  constructor(
    readonly nonComplianceService: NonComplianceService,
    readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  isAviation = this.router.url.includes('/aviation/') ? '/aviation' : '';
  readonly requestInfoId$ = this.nonComplianceService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
  readonly reissuePenalty$ = this.store.payload$.pipe(
    map((payload) => (payload as NonComplianceFinalDeterminationRequestTaskPayload).reissuePenalty),
  );
}
