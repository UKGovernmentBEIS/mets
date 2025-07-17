import { InjectionToken } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';

export interface TaskFormProvider<M, FM extends { [key in keyof FM]: AbstractControl }> {
  form: FormGroup<FM>;
  setFormValue: (formValue: M) => void;
  getFormValue?: () => M;
  destroyForm: () => void;
}

export const TASK_FORM_PROVIDER = new InjectionToken<TaskFormProvider<any, any> | FormGroup>('Task form provider');
