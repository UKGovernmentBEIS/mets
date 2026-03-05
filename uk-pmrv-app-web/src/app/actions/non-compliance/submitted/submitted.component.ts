import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store';

@Component({
  selector: 'app-submitted',
  templateUrl: './submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmittedComponent {
  isAviation$ = this.authStore.pipe(
    selectCurrentDomain,
    map((v) => v === 'AVIATION'),
  );

  constructor(public readonly authStore: AuthStore) {}
}
