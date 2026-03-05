import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { detailSourceDataFormProvider } from '@tasks/aer/verification-submit/compliance-ets/detail-source-data/detail-source-data.form.provider';
import { ComplianceEtsService } from '@tasks/aer/verification-submit/compliance-ets/services/compliance-ets.service';

@Component({
  selector: 'app-detail-source-data',
  templateUrl: './detail-source-data.component.html',
  providers: [detailSourceDataFormProvider, ComplianceEtsService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailSourceDataComponent {
  isEditable$ = this.complianceEtsService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly complianceEtsService: ComplianceEtsService,
  ) {}

  onSubmit(): void {
    this.complianceEtsService.onSubmit('../control-activities', this.form.dirty, this.getFormData());
  }

  getFormData(): Record<string, unknown> {
    return {
      ...this.form.value,
      partOfSiteVerification: !this.form.get('detailSourceDataVerified').value
        ? null
        : this.form.get('partOfSiteVerification').value,
      detailSourceDataNotVerifiedReason: this.form.get('detailSourceDataVerified').value
        ? null
        : this.form.get('detailSourceDataNotVerifiedReason').value,
    };
  }
}
