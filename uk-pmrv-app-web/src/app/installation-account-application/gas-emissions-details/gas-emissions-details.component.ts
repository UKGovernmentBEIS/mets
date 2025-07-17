/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { competentAuthorityMap } from '@shared/interfaces/competent-authority';
import { originalOrder } from '@shared/keyvalue-order';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-gas-emissions-details',
  template: `
    <app-wizard-step
      [showBackLink]="true"
      [formGroup]="form"
      (formSubmit)="onSubmit()"
      heading="Where is the installation located?"
      caption="Installation details">
      <div govuk-radio formControlName="location" hint="Select one option">
        <ng-container govukLegend>
          <span class="govuk-visually-hidden">
            What is the location of the installation which will be producing greenhouse gas emissions?
          </span>
        </ng-container>
        <govuk-radio-option
          *ngFor="let option of options | keyvalue: originalOrder"
          [label]="option.value"
          [value]="option.key"></govuk-radio-option>
      </div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Request to open an installation account</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GasEmissionsDetailsComponent {
  readonly originalOrder = originalOrder;
  form: UntypedFormGroup;
  options = competentAuthorityMap;

  constructor(
    private readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    @Inject(INSTALLATION_FORM) private readonly installationForm: UntypedFormGroup,
  ) {
    this.form = this.installationForm.get('locationGroup') as UntypedFormGroup;
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  onSubmit(): void {
    // The parent form is not submitted, thus not updated
    this.installationForm.updateValueAndValidity();
    this.store.updateTask(ApplicationSectionType.installation, this.installationForm.getRawValue(), 'complete');
    this.store.nextStep('../..', this.route);
  }
}
