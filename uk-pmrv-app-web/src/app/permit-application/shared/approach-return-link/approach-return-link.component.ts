import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-approach-return-link',
  template: `
    <a govukLink [routerLink]="returnToUrl$ | async">Return to: {{ parentTitle }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachReturnLinkComponent {
  @Input() reviewGroupUrl:
    | 'calculation'
    | 'fall-back'
    | 'inherent-co2'
    | 'measurement'
    | 'nitrous-oxide'
    | 'pfc'
    | 'transferred-co2';
  @Input() parentTitle: string;
  @Input() isNested = false;

  returnToUrl$ = combineLatest([this.route.url, this.route.paramMap, this.store]).pipe(
    map(([url, param, state]) => {
      const isIncludedInUrl =
        url.some((segment) => segment.path.includes('summary') || segment.path.includes('answers')) || this.isNested;

      if (
        state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW' ||
        state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW' ||
        state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT' ||
        state.requestTaskType === 'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW' ||
        state.requestTaskType === 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW' ||
        state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW' ||
        state.requestTaskType === 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS' ||
        state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW' ||
        state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW' ||
        state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW' ||
        state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_REVIEW' ||
        state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW' ||
        state.requestTaskType === 'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS' ||
        state.requestTaskType === 'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW' ||
        state.requestActionType === 'PERMIT_ISSUANCE_APPLICATION_GRANTED' ||
        state.requestActionType === 'PERMIT_TRANSFER_B_APPLICATION_GRANTED'
      ) {
        return state.userViewRole === 'REGULATOR'
          ? isIncludedInUrl || param.get('index')
            ? `../../../review/${this.reviewGroupUrl}`
            : `../../review/${this.reviewGroupUrl}`
          : isIncludedInUrl || param.get('index')
            ? '../..'
            : '..';
      }
      return isIncludedInUrl || param.get('index') ? '../..' : '..';
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
