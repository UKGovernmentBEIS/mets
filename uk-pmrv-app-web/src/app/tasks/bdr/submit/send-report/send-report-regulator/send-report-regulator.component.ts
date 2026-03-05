import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';

import { CommonTasksStore } from '../../../../store/common-tasks.store';

@Component({
  selector: 'app-send-report-regulator',
  template: `
    <app-bdr-task>
      <app-page-heading>Send to regulator</app-page-heading>
      <p class="govuk-body">
        Your report will be sent directly to {{ bdrService.competentAuthority$ | async | competentAuthority }}.
      </p>

      <div class="govuk-button-group" *ngIf="bdrService.isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and send</button>
      </div>
      <app-bdr-return-link returnLink="../../"></app-bdr-return-link>
    </app-bdr-task>
  `,
  standalone: true,
  imports: [SharedModule, BdrTaskSharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportRegulatorComponent {
  constructor(
    readonly bdrService: BdrService,
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
            case 'BDR_APPLICATION_SUBMIT':
              actionType = 'BDR_SUBMIT_TO_REGULATOR';
              break;
            case 'BDR_APPLICATION_AMENDS_SUBMIT':
              actionType = 'BDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR';
              break;
          }

          return actionType;
        }),
        switchMap((actionType) => this.bdrService.postSubmit(actionType)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation'], { relativeTo: this.route, queryParams: { sendTo: 'regulator' } });
      });
  }
}
