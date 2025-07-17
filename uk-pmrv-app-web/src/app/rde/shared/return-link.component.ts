import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { RdeStore } from '../store/rde.store';

@Component({
  selector: 'app-return-link',
  template: `
    <a govukLink [routerLink]="returnLink$ | async">Return to: {{ returnText$ | async }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent {
  constructor(
    private readonly route: ActivatedRoute,
    private readonly store: RdeStore,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly returnLink$ = this.returnToLink();
  readonly returnText$ = this.returnToText();

  returnToLink(): Observable<string> {
    return combineLatest([this.taskId$, this.store.pipe(first())]).pipe(
      map(([taskId, state]) => {
        switch (state.requestType) {
          case 'PERMIT_ISSUANCE':
            return `/permit-issuance/${taskId}/review`;
          case 'PERMIT_VARIATION':
            return `/permit-variation/${taskId}/review`;
          case 'PERMIT_SURRENDER':
            return `/permit-surrender/${taskId}/review`;
          case 'PERMIT_TRANSFER_B':
            return `/permit-transfer/${taskId}/review`;
          case 'EMP_ISSUANCE_UKETS':
          case 'EMP_VARIATION_UKETS':
          case 'EMP_VARIATION_CORSIA':
            return `/aviation/tasks/${taskId}`;

          default:
            return '';
        }
      }),
    );
  }

  returnToText(): Observable<string> {
    return this.store.pipe(
      first(),
      map((state) => {
        switch (state.requestType) {
          case 'PERMIT_ISSUANCE':
            return 'Permit Determination';
          case 'PERMIT_VARIATION':
            return 'Permit variation review';
          case 'PERMIT_SURRENDER':
            return 'Permit surrender determination';
          case 'PERMIT_TRANSFER_B':
            return 'Permit Transfer';
          case 'EMP_ISSUANCE_UKETS':
          case 'EMP_ISSUANCE_CORSIA':
            return 'Review emissions monitoring plan application';
          case 'EMP_VARIATION_UKETS':
          case 'EMP_VARIATION_CORSIA':
            return 'Review emissions monitoring plan variation';

          default:
            return '';
        }
      }),
    );
  }
}
