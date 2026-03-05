import { ChangeDetectionStrategy, Component } from '@angular/core';

import { first, map } from 'rxjs';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';
import { ReturnOfAllowancesService } from '../core/return-of-allowances.service';
import { resolveSectionStatus } from '../core/section-status';

@Component({
  selector: 'app-peer-review-wait',
  templateUrl: './peer-review-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewWaitComponent {
  sectionStatus$ = this.returnOfAllowancesService.payload$.pipe(
    first(),
    map((payload) => resolveSectionStatus(payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload)),
  );

  constructor(
    readonly store: CommonTasksStore,
    private readonly returnOfAllowancesService: ReturnOfAllowancesService,
  ) {}
}
