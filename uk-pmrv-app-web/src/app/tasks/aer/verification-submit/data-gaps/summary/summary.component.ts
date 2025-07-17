import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  template: `
    <app-page-heading>Check your answers</app-page-heading>
    <app-data-gaps-group
      [isEditable]="isEditable$ | async"
      [dataGapsInfo]="dataGapsInfo$ | async"></app-data-gaps-group>
    <div *ngIf="(isEditable$ | async) === true" class="govuk-button-group">
      <button appPendingButton govukButton type="button" (click)="onConfirm()">Confirm and complete</button>
    </div>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements OnInit {
  dataGapsInfo$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.methodologiesToCloseDataGaps),
  );
  isEditable$ = this.aerService.isEditable$;

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onConfirm() {
    this.aerService
      .postVerificationTaskSave(null, true, 'methodologiesToCloseDataGaps')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
