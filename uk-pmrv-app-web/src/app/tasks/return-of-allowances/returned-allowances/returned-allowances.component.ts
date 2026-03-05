import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { ReturnOfAllowancesService } from '../core/return-of-allowances.service';
import { resolveReturnedSectionStatus } from '../core/section-status';

@Component({
  selector: 'app-returned-allowances',
  templateUrl: './returned-allowances.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnedAllowancesComponent {
  sectionStatus$ = this.returnOfAllowancesService.payload$.pipe(
    first(),
    map((payload) =>
      resolveReturnedSectionStatus(payload as ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload),
    ),
  );

  canViewSectionDetails$ = combineLatest([this.returnOfAllowancesService.isEditable$, this.sectionStatus$]).pipe(
    map(([isEditable, sectionStatus]) => isEditable || sectionStatus === 'complete'),
  );

  constructor(
    private readonly returnOfAllowancesService: ReturnOfAllowancesService,
    readonly route: ActivatedRoute,
  ) {}
}
