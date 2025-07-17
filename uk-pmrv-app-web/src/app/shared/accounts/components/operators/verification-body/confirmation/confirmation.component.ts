import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store/auth';

@Component({
  selector: 'app-confirmation',
  templateUrl: './confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  @Input() verificationBodyId: number;
  @Input() verificationAccount: string;
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  currentDomain$ = this.authStore.pipe(selectCurrentDomain);
  domainUrlPrefix$ = this.currentDomain$.pipe(map((domain) => (domain === 'AVIATION' ? '/aviation' : '')));
  constructor(
    private readonly route: ActivatedRoute,
    private readonly authStore: AuthStore,
  ) {}
}
