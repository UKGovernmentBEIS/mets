import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { ActivityLevelChangeInformation } from 'pmrv-api';

import { DoalService } from '../../../core/doal.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  editable$: Observable<boolean> = this.doalService.isEditable$;
  activityLevelChangeInformation$: Observable<ActivityLevelChangeInformation> = this.doalService.payload$.pipe(
    map((payload) => payload.doal.activityLevelChangeInformation),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly doalService: DoalService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.doalService
      .saveDoal(undefined, 'activityLevelChangeInformation', true, undefined)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
