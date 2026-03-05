import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { emisionSummaryDetailsFormFactory } from './emission-summary-details-form.provider';

@Component({
  selector: 'app-emission-summary-details',
  templateUrl: './emission-summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emisionSummaryDetailsFormFactory],
})
export class EmissionSummaryDetailsComponent {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => !!paramMap.get('emissionSummaryIndex')));
  requestTaskType$ = this.store.pipe(map((response) => response.requestTaskType));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  onSubmit(): void {
    const summary = this.form.value;

    if (summary.regulatedActivity === 'excludedRegulatedActivity') {
      summary.excludedRegulatedActivity = true;
      summary.regulatedActivity = undefined;
    }

    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => paramMap.get('emissionSummaryIndex')),
        withLatestFrom(this.store.getTask('emissionSummaries')),
        switchMap(([emissionSummaryIndex, emissionSummaries]) =>
          this.store.postTask(
            'emissionSummaries',
            emissionSummaryIndex
              ? emissionSummaries.map((item, index) => (Number(emissionSummaryIndex) === index ? summary : item))
              : [...emissionSummaries, summary],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
