import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-bdrs2-send-report-regulator',
  template: `
    <app-bdrs2-task returnLink="../.." [returnLinkTitle]="returnLinkTitle" [breadcrumb]="true">
      <app-page-heading>Send to regulator</app-page-heading>
      <p class="govuk-body">
        Your report will be sent directly to your regulator ({{
          bdrs2Service.competentAuthority$ | async | competentAuthority
        }}).
      </p>
      <p class="govuk-body">
        By selecting ‘Confirm and send’ you confirm that the information in your report is correct to the best of your
        knowledge.
      </p>

      <div class="govuk-button-group" *ngIf="bdrs2Service.isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and send</button>
      </div>
    </app-bdrs2-task>
  `,
  standalone: true,
  imports: [SharedModule, BdrS2TaskSharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2SendReportRegulatorComponent {
  returnLinkTitle = this.bdrs2Service.title();

  constructor(
    readonly bdrs2Service: BdrS2Service,
    private readonly store: CommonTasksStore,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.requestTaskType$
      .pipe(
        first(),
        map((requestTaskType) => {
          let actionType;

          switch (requestTaskType) {
            case 'BDRS2_APPLICATION_SUBMIT':
              actionType = 'BDRS2_SUBMIT_TO_REGULATOR';
              break;
          }

          return actionType;
        }),
        switchMap((actionType) => this.bdrs2Service.postSubmit(actionType)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation'], { relativeTo: this.route, queryParams: { sendTo: 'regulator' } });
      });
  }
}
