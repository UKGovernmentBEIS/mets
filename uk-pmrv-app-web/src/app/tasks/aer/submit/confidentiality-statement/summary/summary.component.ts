import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary',
  template: `
    <app-aer-task [breadcrumb]="true">
      <app-page-heading>Check your answers</app-page-heading>
      <app-confidentiality-statement-summary-template
        [isEditable]="isEditable$ | async"
        [data]="confidentialityStatement$ | async"></app-confidentiality-statement-summary-template>
      <div class="govuk-button-group" *ngIf="isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and complete</button>
      </div>
      <app-return-link></app-return-link>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;
  confidentialityStatement$ = (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer.confidentialityStatement),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.aerService
      .postTaskSave(undefined, undefined, true, 'confidentialityStatement')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
