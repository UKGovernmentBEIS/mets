import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  grantDetermination$ = this.store.pipe(
    map((state) => state?.reviewDetermination),
  ) as Observable<PermitSurrenderReviewDeterminationGrant>;
  isEditable$ = this.store.pipe(map((state) => state?.isEditable));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitSurrenderStore,
    private readonly router: Router,
  ) {}
}
