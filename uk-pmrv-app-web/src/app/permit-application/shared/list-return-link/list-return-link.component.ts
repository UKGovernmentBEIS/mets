import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-list-return-link',
  template: '<a govukLink [routerLink]="link$ | async">Return to: {{ title }}</a>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListReturnLinkComponent {
  @Input() reviewGroupUrl: string;
  @Input() reviewGroupTitle: string;
  @Input() innerRoute = false;
  @Input() doubleInner: boolean;

  title: string;

  link$ = combineLatest([this.route.url, this.store]).pipe(
    map(([url, state]) => {
      const isIncludedInUrl = url.some(
        (segment) =>
          segment.path.includes('summary') ||
          ((this.reviewGroupUrl === 'monitoring-methodology-plan' || this.reviewGroupUrl === 'uncertainty-analysis') &&
            (segment.path.includes('answers') ||
              segment.path.includes('upload-file') ||
              segment.path.includes('connections'))),
      );
      let innerRoutePath = this.innerRoute ? '../' : this.doubleInner === false ? '../' : '';

      if (this.doubleInner === true) {
        innerRoutePath = this.innerRoute ? '../../' : '../';
      }

      if (
        [
          'PERMIT_ISSUANCE_APPLICATION_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
          'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
          'PERMIT_VARIATION_APPLICATION_REVIEW',
          'PERMIT_VARIATION_APPLICATION_PEER_REVIEW',
          'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
          'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW',
          'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
          'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW',
          'PERMIT_TRANSFER_B_APPLICATION_REVIEW',
          'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW',
          'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS',
          'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW',
        ].some((type) => type === state.requestTaskType) ||
        ['PERMIT_ISSUANCE_APPLICATION_GRANTED', 'PERMIT_TRANSFER_B_APPLICATION_GRANTED'].some(
          (type) => type === state.requestActionType,
        )
      ) {
        this.title = state.userViewRole === 'REGULATOR' ? this.reviewGroupTitle : this.store.getApplyForHeader();

        return state.userViewRole === 'REGULATOR'
          ? isIncludedInUrl
            ? `${innerRoutePath}../../review/${this.reviewGroupUrl}`
            : `${innerRoutePath}../review/${this.reviewGroupUrl}`
          : isIncludedInUrl
            ? `${innerRoutePath}../..`
            : `${innerRoutePath}..`;
      }

      if (['PERMIT_VARIATION_APPLICATION_SUBMIT'].some((type) => type === state.requestTaskType)) {
        this.title = 'Apply for a permit variation';
        return `/permit-variation/${state.requestTaskId}`;
      }

      //default
      this.title = this.store.getApplyForHeader();
      return isIncludedInUrl ? `${innerRoutePath}../..` : `${innerRoutePath}..`;
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}
}
