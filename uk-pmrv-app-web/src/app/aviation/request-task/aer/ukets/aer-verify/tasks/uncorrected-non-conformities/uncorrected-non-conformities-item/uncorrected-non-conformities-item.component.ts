import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';

@Component({
  selector: 'app-uncorrected-non-conformities-item',
  templateUrl: './uncorrected-non-conformities-item.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesItemComponent implements OnInit {
  protected form = this.formProvider.createUncorrectedNonConformitiesGroup();

  get index() {
    return +this.route.snapshot.paramMap.get('index');
  }

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: UncorrectedNonConformitiesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  ngOnInit(): void {
    const uncorrectedNonConformities = this.formProvider.getFormValue().uncorrectedNonConformities;

    if (uncorrectedNonConformities && !!uncorrectedNonConformities[this.index]) {
      this.form.patchValue(uncorrectedNonConformities[this.index]);
    }
  }

  onSubmit() {
    const uncorrectedNonConformities = this.formProvider.getFormValue().uncorrectedNonConformities
      ? [...this.formProvider.getFormValue().uncorrectedNonConformities]
      : [];

    const data =
      this.index >= (uncorrectedNonConformities?.length ?? 0)
        ? [...(uncorrectedNonConformities ?? []), { ...this.form.value, reference: `B${this.index + 1}` }]
        : uncorrectedNonConformities.map((item, idx) => (idx === this.index ? { ...item, ...this.form.value } : item));

    this.formProvider.setFormValue({ ...this.formProvider.getFormValue(), uncorrectedNonConformities: data });

    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setUncorrectedNonConformities({
      ...this.formProvider.getFormValue(),
      uncorrectedNonConformities: data,
    });

    this.router.navigate(['..'], {
      relativeTo: this.route,
      replaceUrl: true,
      queryParams: { change: true },
    });
  }
}
