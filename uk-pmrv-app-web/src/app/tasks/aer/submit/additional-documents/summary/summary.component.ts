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
      <h2 app-summary-header class="govuk-heading-m">
        <span [class.govuk-visually-hidden]="(additionalDocumentFiles$ | async).length === 0">
          Uploaded additional documents and information
        </span>
      </h2>
      <app-documents-summary-template
        [isEditable]="isEditable$ | async"
        [data]="additionalDocuments$ | async"
        [files]="additionalDocumentFiles$ | async"></app-documents-summary-template>
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
  additionalDocuments$ = (this.aerService.getPayload() as Observable<AerApplicationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer.additionalDocuments),
  );
  additionalDocumentFiles$ = this.additionalDocuments$.pipe(
    map((additionalDocuments) =>
      additionalDocuments?.exist ? this.aerService.getDownloadUrlFiles(additionalDocuments.documents) : [],
    ),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.aerService
      .postTaskSave(undefined, undefined, true, 'additionalDocuments')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
