import { ChangeDetectionStrategy, Component, ViewChild } from '@angular/core';
import { FormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { GovukValidators } from 'govuk-components';

import { EmpBatchReissueStore } from '../store/emp-batch-reissue.store';

@Component({
  selector: 'app-changes',
  imports: [SharedModule],
  templateUrl: './changes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangesComponent {
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  existingChangesDetails = this.store.getState().changesDetails;

  form: UntypedFormGroup = this.formBuilder.group(
    {
      changes: this.formBuilder.array(
        this.existingChangesDetails?.['changes']?.length
          ? this.existingChangesDetails?.['changes'].map((change) => this.formBuilder.control(change))
          : [this.formBuilder.control(this.existingChangesDetails?.['changes']?.[0] || '')],
      ),
    },
    {
      validators: [atLeastOneRequiredValidator('Enter a change to include in the variation schedule')],
      updateOn: 'change',
    },
  );

  get changesFormArray(): FormArray {
    return this.form.get('changes') as FormArray;
  }

  addAnotherItem() {
    this.changesFormArray.push(new UntypedFormControl(''));
  }

  constructor(
    private readonly formBuilder: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: EmpBatchReissueStore,
  ) {}

  onSubmit(): void {
    const newChangesDetails = {
      changesDetails: {
        ...this.existingChangesDetails,
        ...this.form.value,
      },
    };
    this.store.patchState(newChangesDetails);

    this.router.navigate(['..', 'signatory'], { relativeTo: this.route });
  }
}

function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: UntypedFormGroup) => {
    const changesArray = group.get('changes') as FormArray;
    if (!changesArray) return { atLeastOneRequired: true };

    // Check if at least one control in the array has a non-empty value
    const hasAtLeastOneValue = changesArray.controls.some(
      (control) => control.value && control.value.trim().length > 0,
    );

    return hasAtLeastOneValue ? null : { atLeastOneRequired: true };
  });
}
