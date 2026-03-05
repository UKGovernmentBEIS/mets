import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { uncorrectedMisstatementsQuery } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.selector';
import { UncorrectedMisstatementsFormProvider } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedMisstatements } from 'pmrv-api';

import { AER_VERIFY_TASK_FORM, MisstatementItemFormProvider } from './misstatement-item-form.provider';

interface ViewModel {
  isEditable: boolean;
}

@Component({
  selector: 'app-misstatement-item',
  templateUrl: './misstatement-item.component.html',
  providers: [DestroySubject, MisstatementItemFormProvider],
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementItemComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectIsEditable)]).pipe(
    map(([isEditable]) => ({
      isEditable,
    })),
  );

  constructor(
    @Inject(AER_VERIFY_TASK_FORM) readonly form: UntypedFormGroup,
    @Inject(TASK_FORM_PROVIDER) readonly parentFormProvider: UncorrectedMisstatementsFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      let misstatementValue: AviationAerUncorrectedMisstatements;
      combineLatest([
        this.store.pipe(first(), uncorrectedMisstatementsQuery.selectUncorrectedMisstatements),
        this.route.paramMap,
      ])
        .pipe(
          first(),
          switchMap(([uncorrectedMisstatements, paramMap]) => {
            const index = +paramMap.get('index');

            misstatementValue = {
              ...uncorrectedMisstatements,
              uncorrectedMisstatements:
                index >= (uncorrectedMisstatements?.uncorrectedMisstatements?.length ?? 0)
                  ? [
                      ...(uncorrectedMisstatements?.uncorrectedMisstatements ?? []),
                      { ...this.form.value, reference: `A${index + 1}` },
                    ]
                  : uncorrectedMisstatements.uncorrectedMisstatements.map((item, idx) =>
                      idx === index ? { ...item, ...this.form.value } : item,
                    ),
            };

            return this.store.aerVerifyDelegate.saveAerVerify(
              {
                uncorrectedMisstatements: misstatementValue,
              },
              'in progress',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.parentFormProvider.setFormValue({ ...misstatementValue });
          this.nextUrl();
        });
    }
  }

  private nextUrl() {
    return this.router.navigate(['..'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
