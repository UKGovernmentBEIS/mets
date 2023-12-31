import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { GovukSelectOption } from 'govuk-components';

import { CoordinatesDTO } from 'pmrv-api';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-offshore-details',
  templateUrl: './offshore-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OffshoreDetailsComponent {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  backLink = true;
  form: UntypedFormGroup;
  latitudeDirections: GovukSelectOption<CoordinatesDTO['cardinalDirection']>[] = [
    { text: 'North', value: 'NORTH' },
    { text: 'South', value: 'SOUTH' },
  ];
  longitudeDirections: GovukSelectOption<CoordinatesDTO['cardinalDirection']>[] = [
    { text: 'East', value: 'EAST' },
    { text: 'West', value: 'WEST' },
  ];

  constructor(
    public readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    @Inject(INSTALLATION_FORM) private readonly installationForm: UntypedFormGroup,
  ) {
    this.installationForm.get('onshoreGroup').disable();
    this.form = this.installationForm.get('offshoreGroup') as UntypedFormGroup;
    this.form.enable();
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  onSubmit(): void {
    // The parent form is not submitted, thus not updated
    this.installationForm.updateValueAndValidity();
    this.store.updateTask(ApplicationSectionType.installation, this.installationForm.value, 'complete');
    if (this.store.getState().isReviewed) {
      this.store.amend().subscribe(() => this.store.nextStep('../..', this.route));
    } else {
      this.store.nextStep('../..', this.route);
    }
  }
}
