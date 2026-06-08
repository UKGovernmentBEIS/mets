import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

export const TASKS_RETURN_TO_OPERATOR_FORM = new InjectionToken<UntypedFormGroup>('Return to operator tasks form');
