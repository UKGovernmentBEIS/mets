/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { map } from 'rxjs';

import { AuthStore } from '@core/store';

import { BusinessErrorService } from './business-error.service';

@Component({
  selector: 'app-business-error',
  standalone: false,
  template: `
    <app-error-page *ngIf="businessErrorService.error$ | async as error" [heading]="error.heading">
      <p class="govuk-body">
        @if (error.accountId) {
          <div>
            <a
              govukLink
              [routerLink]="[
                isAviation() ? '../../aviation/accounts/' : '../../accounts/',
                error.accountId,
                'verification-body',
                'appoint',
              ]">
              Appoint a verifier
            </a>
            for your account. Once you have appointed a verifier, go to your account dashboard and select the relevant
            task to continue.
          </div>
          <br />
        }
        <a govukLink [routerLink]="error.link" [fragment]="error.fragment">{{ error.linkText }}</a>
      </p>
    </app-error-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BusinessErrorComponent implements OnDestroy {
  isAviation = toSignal(this.authStore.pipe(map((state) => state.currentDomain === 'AVIATION')));

  constructor(
    readonly businessErrorService: BusinessErrorService,
    private readonly authStore: AuthStore,
  ) {}

  ngOnDestroy(): void {
    this.businessErrorService.clear();
  }
}
