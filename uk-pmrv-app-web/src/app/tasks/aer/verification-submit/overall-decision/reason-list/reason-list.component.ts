import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload, VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

@Component({
  selector: 'app-reason-list',
  templateUrl: './reason-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonListComponent {
  isEditable$ = this.aerService.isEditable$;
  reasons$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => (payload.verificationReport.overallAssessment as VerifiedWithCommentsOverallAssessment)?.reasons),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue() {
    return this.router.navigate(['../summary'], { relativeTo: this.route });
  }
}
