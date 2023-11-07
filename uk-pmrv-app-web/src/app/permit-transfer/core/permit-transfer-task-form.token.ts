import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

export const PERMIT_TRANSFER_B_FORM = new InjectionToken<UntypedFormGroup>('Permit transfer receiving form');
