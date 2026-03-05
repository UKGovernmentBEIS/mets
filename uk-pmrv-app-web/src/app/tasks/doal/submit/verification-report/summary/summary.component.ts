import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { DoalService } from '../../../core/doal.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  isEditable$ = this.doalService.isEditable$;

  verificationActivityLevelReport$ = this.doalService.payload$.pipe(
    map((payload) => payload.doal?.verificationReportOfTheActivityLevelReport),
  );

  documentFile$ = this.verificationActivityLevelReport$.pipe(
    map((erificationActivityLevelReport) => erificationActivityLevelReport?.document),
    map((file) => this.doalService.getDownloadUrlFile(file)),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.doalService
      .saveDoal(undefined, 'verificationReportOfTheActivityLevelReport', true, undefined)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
