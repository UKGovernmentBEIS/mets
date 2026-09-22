import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

export const CHANGE_PAYMENT_AMOUNT_FORM = new InjectionToken<UntypedFormGroup>('CHANGE_PAYMENT_AMOUNT_FORM');
