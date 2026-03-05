import { NgFor, NgIf } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { AbstractControl, FormArray } from '@angular/forms';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { AbbreviationsFormProvider } from '../abbreviations-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-abbreviations-form',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, NgFor, SharedModule],
  templateUrl: './abbreviations-form.component.html',
  providers: [DestroySubject],
  viewProviders: [existingControlContainer],
})
export class AbbreviationsFormComponent {
  @Input() heading: HTMLHeadingElement;

  formProvider = inject<AbbreviationsFormProvider>(TASK_FORM_PROVIDER);
  form = this.formProvider.form;

  addAbbreviationDefinitionCtrl() {
    this.formProvider.addAbbreviationDefinitionCtrl();
  }

  removeAbbreviationDefinitionCtrl(index: number) {
    this.formProvider.removeAbbreviationDefinitionCtrl(index);
  }

  get abbreviationDefinitionsCtrl(): FormArray | null {
    return (this.form.get('abbreviationDefinitions') as FormArray) ?? null;
  }

  get existCtrl(): AbstractControl {
    return this.form.get('exist');
  }
}
