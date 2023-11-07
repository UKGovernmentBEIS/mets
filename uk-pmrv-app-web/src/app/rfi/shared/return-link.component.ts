import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { RfiStore } from '../store/rfi.store';

@Component({
  selector: 'app-return-link',
  template: ` <a govukLink [routerLink]="returnLink$ | async"> Return to: {{ returnText$ | async }} </a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent {
  constructor(private readonly route: ActivatedRoute, private readonly store: RfiStore) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly returnLink$ = this.returnToLink();
  readonly returnText$ = this.returnToText();

  returnToLink(): Observable<string> {
    return combineLatest([this.taskId$, this.store.pipe(first())]).pipe(
      map(([taskId, state]) => {
        switch (state.requestType) {
          case 'PERMIT_ISSUANCE':
            return `/permit-issuance/${taskId}/review`;
          case 'PERMIT_TRANSFER_B':
            return `/permit-transfer/${taskId}/review`;
          case 'PERMIT_VARIATION':
            return `/permit-variation/${taskId}/review`;
          case 'PERMIT_SURRENDER':
            return `/permit-surrender/${taskId}/review`;
          case 'PERMIT_NOTIFICATION':
            return `/tasks/${taskId}/permit-notification/review`;
          case 'AIR':
            return `/tasks/${taskId}/air/review`;
          case 'VIR':
            return `/tasks/${taskId}/vir/review`;
          case 'EMP_ISSUANCE_UKETS':
          case 'EMP_VARIATION_UKETS':
          case 'EMP_VARIATION_CORSIA':
          case 'AVIATION_VIR':
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
          case 'PERMIT_TRANSFER_B':
            return 'Permit Transfer';
          case 'PERMIT_VARIATION':
            return 'Permit variation review';
          case 'PERMIT_SURRENDER':
            return 'Permit surrender determination';
          case 'PERMIT_NOTIFICATION':
            return 'Permit notification determination';
          case 'AIR':
            return 'Annual improvement report';
          case 'VIR':
            return 'Verification improvement report';
          case 'EMP_ISSUANCE_UKETS':
            return 'Review emissions monitoring plan application';
          case 'EMP_VARIATION_UKETS':
          case 'EMP_VARIATION_CORSIA':
            return 'Review emissions monitoring plan variation';
          case 'AVIATION_VIR':
            return 'Review verifier improvement report';

          default:
            return '';
        }
      }),
    );
  }
}
