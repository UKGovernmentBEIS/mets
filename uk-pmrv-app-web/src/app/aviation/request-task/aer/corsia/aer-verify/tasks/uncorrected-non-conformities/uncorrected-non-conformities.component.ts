import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { UncorrectedNonConformitiesFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/uncorrected-non-conformities-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaUncorrectedNonConformities } from 'pmrv-api';

@Component({
  selector: 'app-uncorrected-non-conformities',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink],
  templateUrl: './uncorrected-non-conformities.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UncorrectedNonConformitiesComponent {
  form = new FormGroup({
    existUncorrectedNonConformities: this.formProvider.existCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: UncorrectedNonConformitiesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store
      .pipe(first(), aerVerifyCorsiaQuery.selectUncorrectedNonConformities)
      .pipe(
        switchMap((uncorrectedNonConformities) => {
          const value = this.form.get('existUncorrectedNonConformities').value
            ? {
                ...uncorrectedNonConformities,
                ...this.form.value,
              }
            : ({
                ...this.form.value,
                uncorrectedNonConformities: [],
              } as AviationAerCorsiaUncorrectedNonConformities);

          this.formProvider.setFormValue(value);
          return (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).saveAerVerify(
            {
              uncorrectedNonConformities: value,
            },
            'in progress',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.nextUrl());
  }

  private nextUrl() {
    switch (this.form.get('existUncorrectedNonConformities').value) {
      case false:
        return this.router.navigate(['summary'], { relativeTo: this.route, queryParams: { change: true } });
      case true:
        if (this.formProvider.getFormValue()?.uncorrectedNonConformities?.length > 0) {
          return this.router.navigate(['list'], { relativeTo: this.route, queryParams: { change: true } });
        } else {
          return this.router.navigate(['list/0'], { relativeTo: this.route, queryParams: { change: true } });
        }
    }
  }
}
