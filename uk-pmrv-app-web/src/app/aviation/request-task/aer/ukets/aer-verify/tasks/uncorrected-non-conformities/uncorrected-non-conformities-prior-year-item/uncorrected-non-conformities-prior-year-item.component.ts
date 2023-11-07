import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
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
  selector: 'app-uncorrected-non-conformities-prior-year-item',
  templateUrl: './uncorrected-non-conformities-prior-year-item.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesPriorYearItemComponent implements OnInit {
  protected form = this.formProvider.createPriorYearIssuesGroup();

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
    const priorYearIssues = this.formProvider.getFormValue().priorYearIssues;

    if (priorYearIssues && !!priorYearIssues[this.index]) {
      this.form.patchValue(priorYearIssues[this.index]);
    }
  }

  onSubmit() {
    const priorYearIssues = this.formProvider.getFormValue().priorYearIssues
      ? [...this.formProvider.getFormValue().priorYearIssues]
      : [];

    const data =
      this.index >= (priorYearIssues?.length ?? 0)
        ? [...(priorYearIssues ?? []), { ...this.form.value, reference: `E${this.index + 1}` }]
        : priorYearIssues.map((item, idx) => (idx === this.index ? { ...item, ...this.form.value } : item));

    this.formProvider.setFormValue({ ...this.formProvider.getFormValue(), priorYearIssues: data });

    (this.store.aerVerifyDelegate as AerVerifyStoreDelegate).setUncorrectedNonConformities({
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
