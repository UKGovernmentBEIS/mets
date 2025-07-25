import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AbstractControl } from '@angular/forms';

import { debounceTime, map, Observable } from 'rxjs';

import { zxcvbn } from '@zxcvbn-ts/core';
import CryptoJS from 'crypto-js';

import { MessageValidationErrors } from 'govuk-components';

@Injectable()
export class PasswordService {
  constructor(private readonly http: HttpClient) {}

  isBlacklistedPassword(password: string): Observable<boolean> {
    const hexString = CryptoJS.SHA1(password).toString();
    const prefix = hexString.substr(0, 5);
    return this.http
      .get(`https://api.pwnedpasswords.com/range/${prefix}`, {
        headers: new HttpHeaders({ 'Content-Type': 'text/plain' }),
        responseType: 'text',
      })
      .pipe(map((v) => v.indexOf(hexString.substr(5).toUpperCase()) >= 0));
  }

  blacklisted(control: AbstractControl): Observable<MessageValidationErrors | null> {
    return this.isBlacklistedPassword(control.value).pipe(
      debounceTime(500),
      map((isBlacklisted: boolean) =>
        isBlacklisted ? { blacklisted: 'For security reasons you must change your password' } : null,
      ),
    );
  }

  strong(control: AbstractControl): MessageValidationErrors | null {
    const strength = zxcvbn(control.value ?? '').score;

    return strength > 2 ? null : { weakPassword: 'Enter a strong password' };
  }
}
