import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';

@Component({
  selector: 'app-uncorrected-non-conformities-page',
  templateUrl: './uncorrected-non-conformities-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesPageComponent {
  protected form = new FormGroup({
    existUncorrectedNonConformities: this.formProvider.existUncorrectedNonConformitiesCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: UncorrectedNonConformitiesFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.invalid) return;

    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate)
      .saveAerVerify({ uncorrectedNonConformities: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        let path: string;

        if (this.form.value.existUncorrectedNonConformities) {
          path = 'uncorrected-list';
        } else {
          path = 'prior-year-issues';
        }

        (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setUncorrectedNonConformities(
          this.formProvider.getFormValue(),
        );
        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}
