import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';

@Component({
  selector: 'app-changes-summary',
  imports: [SharedModule],
  templateUrl: './changes-summary.component.html',
  styles: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangesSummaryComponent {
  existingChangesDetails = this.store.getState().changesDetails;
  form: UntypedFormGroup = this.formBuilder.group({
    changesSummary: [
      this.existingChangesDetails?.['changesSummary'] || '',
      {
        validators: [
          GovukValidators.required('Enter a summary of changes for the permit variation log'),
          GovukValidators.maxLength(10000, `The summary should not be more than 10000 characters`),
        ],
      },
    ],
  });

  constructor(
    private readonly formBuilder: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitBatchReissueStore,
  ) {}

  onSubmit(): void {
    if (this.form.dirty) {
      const newChangesDetails = {
        changesDetails: {
          ...this.existingChangesDetails,
          ...this.form.value,
        },
      };
      this.store.patchState(newChangesDetails);
    }
    this.router.navigate(['..', 'changes'], { relativeTo: this.route });
  }
}
