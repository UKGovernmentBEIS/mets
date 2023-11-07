import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { reviewRequestTaskTypes } from '../../shared/utils/permit';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { emissionPointFormProvider } from './emission-point-details-form.provider';

@Component({
  selector: 'app-emission-point-details',
  template: `
    <app-permit-task
      [breadcrumb]="
        (requestTaskType$ | async)
          ? [
              {
                text: 'Fuels and equipment inventory',
                link: ['fuels']
              }
            ]
          : [{ text: 'Emission points', link: ['emission-points'] }]
      "
    >
      <app-emission-point-details-template (formSubmit)="onSubmit()" [form]="form" [isEditing]="isEditing$ | async">
      </app-emission-point-details-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, emissionPointFormProvider],
})
export class EmissionPointDetailsComponent {
  isEditing$ = this.store
    .getTask('emissionPoints')
    .pipe(map((emissionPoints) => emissionPoints.some((item) => item.id === this.form.get('id').value)));
  requestTaskType$ = this.store.pipe(map((response) => reviewRequestTaskTypes.includes(response.requestTaskType)));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postTask(
            'emissionPoints',
            state.permit.emissionPoints.some((point) => point.id === this.form.value.id)
              ? state.permit.emissionPoints.map((point) => (point.id === this.form.value.id ? this.form.value : point))
              : [...state.permit.emissionPoints, this.form.value],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
