import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

export const NER_TASK_FORM = new InjectionToken<UntypedFormGroup>('NER task form');
