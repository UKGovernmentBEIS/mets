import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/shared/util/aer.util';
import { AER_TASK_FORM } from '@aviation/request-task/aer/ukets/tasks/send-report/aer-submit-token';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { sendReportFormProvider } from './send-report-form.provider';

export interface SendReportViewModel {
  header: string;
  competentAuthority: 'ENGLAND' | 'NORTHERN_IRELAND' | 'OPRED' | 'SCOTLAND' | 'WALES';
  submitHidden: boolean;
}

@Component({
  selector: 'app-send-report-page',
  templateUrl: './send-report-page.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  standalone: true,
  providers: [DestroySubject, sendReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportPageComponent {
  isSendReportAvailable$ = this.store.pipe(
    aerQuery.selectPayload,
    map(
      (payload) =>
        getTaskStatusByTaskCompletionState(
          'sendReport',
          payload,
          payload.payloadType === 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
        ) !== 'cannot start yet',
    ),
  );

  vm$: Observable<SendReportViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectCompetentAuthority),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([authority, isEditable]) => {
      return {
        header: 'Submit your report',
        competentAuthority: authority,
        submitHidden: !isEditable,
      };
    }),
  );

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    protected readonly store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.get('option').value) {
      this.router.navigate(['verification'], { relativeTo: this.route });
    } else {
      this.router.navigate(['regulator'], { relativeTo: this.route });
    }
  }
}
