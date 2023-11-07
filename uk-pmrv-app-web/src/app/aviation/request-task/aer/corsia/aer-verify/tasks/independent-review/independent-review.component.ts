import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';

import { map, Observable } from 'rxjs';

import { IndependentReviewFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/independent-review/independent-review-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { CountryService } from '@core/services/country.service';
import { SharedModule } from '@shared/shared.module';

import { GovukSelectOption } from 'govuk-components';

@Component({
  selector: 'app-independent-review',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, RouterLink, RouterLinkActive],
  templateUrl: './independent-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndependentReviewComponent {
  form = this.formProvider.form;
  countriesOptions$: Observable<GovukSelectOption<string>[]> = this.countryService
    .getUkCountries()
    .pipe(
      map((countries) =>
        countries
          .map((country) => ({ text: country.name, value: country.code } as GovukSelectOption<string>))
          .sort((a, b) => a.text.localeCompare(b.text)),
      ),
    );

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: IndependentReviewFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly countryService: CountryService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify(
        {
          independentReview: this.formProvider.getFormValue(),
        },
        'in progress',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}
