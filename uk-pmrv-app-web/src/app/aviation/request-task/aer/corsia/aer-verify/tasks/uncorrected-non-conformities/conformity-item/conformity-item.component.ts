import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import {
  AER_VERIFY_TASK_FORM,
  ConformityItemFormProvider,
} from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/conformity-item/conformity-item-form.provider';
import { UncorrectedNonConformitiesFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/uncorrected-non-conformities-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaUncorrectedNonConformities } from 'pmrv-api';

@Component({
  selector: 'app-conformity-item',
  templateUrl: './conformity-item.component.html',
  providers: [DestroySubject, ConformityItemFormProvider],
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConformityItemComponent {
  constructor(
    @Inject(AER_VERIFY_TASK_FORM) readonly form: UntypedFormGroup,
    @Inject(TASK_FORM_PROVIDER) readonly parentFormProvider: UncorrectedNonConformitiesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      let conformityValue: AviationAerCorsiaUncorrectedNonConformities;
      combineLatest([
        this.store.pipe(first(), aerVerifyCorsiaQuery.selectUncorrectedNonConformities),
        this.route.paramMap,
      ])
        .pipe(
          first(),
          switchMap(([uncorrectedNonConformities, paramMap]) => {
            const index = +paramMap.get('index');

            conformityValue = {
              ...uncorrectedNonConformities,
              uncorrectedNonConformities:
                index >= (uncorrectedNonConformities?.uncorrectedNonConformities?.length ?? 0)
                  ? [
                      ...(uncorrectedNonConformities?.uncorrectedNonConformities ?? []),
                      { ...this.form.value, reference: `B${index + 1}` },
                    ]
                  : uncorrectedNonConformities.uncorrectedNonConformities.map((item, idx) =>
                      idx === index ? { ...item, ...this.form.value } : item,
                    ),
            };

            return (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).saveAerVerify(
              {
                uncorrectedNonConformities: conformityValue,
              },
              'in progress',
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          this.parentFormProvider.setFormValue({ ...conformityValue });
          this.nextUrl();
        });
    }
  }

  private nextUrl() {
    return this.router.navigate(['..'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
