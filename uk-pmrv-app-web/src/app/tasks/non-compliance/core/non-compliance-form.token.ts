import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

export const NON_COMPLIANCE_TASK_FORM = new InjectionToken<UntypedFormGroup>('Non compliance task form');
