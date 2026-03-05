import { ChangeDetectionStrategy, Component, Inject, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { noticeDateFormProvider } from '../notice-date/notice-date-form.provider';

@Component({
  selector: 'app-grant-determination-summary-details',
  templateUrl: './summary-details.component.html',
  styleUrl: './summary-details.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [noticeDateFormProvider],
})
export class SummaryDetailsComponent {
  @Input() grantDetermination$: Observable<PermitSurrenderReviewDeterminationGrant>;

  isEditable$ = this.store.pipe(map((state) => state?.isEditable));

  invalidNoticeDate$ = this.store.pipe(
    tap(() => {
      this.form.controls.noticeDate.enable();
      this.form.controls.noticeDate.updateValueAndValidity();
    }),
    map(() => this.form.controls.noticeDate.errors?.invalidNoticeDate),
  );

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly store: PermitSurrenderStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    const baseUrl = !wizardStep ? '../../' : '../';
    this.router.navigate([baseUrl + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
