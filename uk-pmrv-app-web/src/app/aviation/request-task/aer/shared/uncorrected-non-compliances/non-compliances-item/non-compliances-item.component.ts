import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { uncorrectedNonCompliancesQuery } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedNonCompliances } from 'pmrv-api';

import { UncorrectedNonCompliancesFormProvider } from '../uncorrected-non-compliances-form.provider';
import { AER_VERIFY_TASK_FORM, NonCompliancesItemFormProvider } from './non-compliances-item-form.provider';

interface ViewModel {
  pageHeader: string;
  isCorsia: boolean;
  isEditable: boolean;
}

@Component({
  selector: 'app-non-compliances-item',
  templateUrl: './non-compliances-item.component.html',
  providers: [DestroySubject, NonCompliancesItemFormProvider],
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesItemComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectIsCorsia),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([isCorsia, isEditable]) => ({
      pageHeader: isCorsia
        ? 'Add a non-compliance with the Air Navigation Order'
        : 'Add a non-compliance with the monitoring and reporting regulations',
      isCorsia,
      isEditable,
    })),
  );

  constructor(
    @Inject(AER_VERIFY_TASK_FORM) readonly form: UntypedFormGroup,
    @Inject(TASK_FORM_PROVIDER) readonly parentFormProvider: UncorrectedNonCompliancesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      let nonCompliancesValue: AviationAerUncorrectedNonCompliances;
      combineLatest([
        this.store.pipe(first(), uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances),
        this.route.paramMap,
      ])
        .pipe(
          first(),
          switchMap(([uncorrectedNonCompliances, paramMap]) => {
            const index = +paramMap.get('index');
            nonCompliancesValue = {
              ...uncorrectedNonCompliances,
              uncorrectedNonCompliances:
                index >= (uncorrectedNonCompliances?.uncorrectedNonCompliances?.length ?? 0)
                  ? [
                      ...(uncorrectedNonCompliances?.uncorrectedNonCompliances ?? []),
                      { ...this.form.value, reference: `C${index + 1}` },
                    ]
                  : uncorrectedNonCompliances.uncorrectedNonCompliances.map((item, idx) =>
                      idx === index ? { ...item, ...this.form.value } : item,
                    ),
            };

            return this.store.aerVerifyDelegate.saveAerVerify(
              {
                uncorrectedNonCompliances: nonCompliancesValue,
              },
              'in progress',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.parentFormProvider.setFormValue({ ...nonCompliancesValue });
          this.nextUrl();
        });
    }
  }

  private nextUrl() {
    return this.router.navigate(['..'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
