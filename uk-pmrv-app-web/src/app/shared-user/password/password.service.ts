import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AbstractControl } from '@angular/forms';

import { zxcvbn } from '@zxcvbn-ts/core';

import { MessageValidationErrors } from 'govuk-components';

@Injectable()
export class PasswordService {
  constructor(private readonly http: HttpClient) {}

  strong(control: AbstractControl): MessageValidationErrors | null {
    const strength = zxcvbn(control.value ?? '').score;

    return strength > 2 ? null : { weakPassword: 'Enter a strong password' };
  }
}
