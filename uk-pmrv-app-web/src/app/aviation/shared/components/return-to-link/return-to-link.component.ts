import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, RouterLinkWithHref } from '@angular/router';

import { BehaviorSubject, first, map, Observable } from 'rxjs';

import { TYPE_AWARE_STORE, TypeAwareStore } from '@aviation/type-aware.store';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-return-to-link',
  standalone: true,
  imports: [CommonModule, RouterLinkWithHref, GovukComponentsModule],
  template: `
    <a govukLink [routerLink]="returnToUrl$ | async">Return to: {{ returnText$ | async }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnToLinkComponent {
  constructor(
    private readonly route: ActivatedRoute,
    @Inject(TYPE_AWARE_STORE) private readonly store: TypeAwareStore,
  ) {
    let returnRoute = this.route;
    while (returnRoute.parent && ![':actionId', ':taskId'].includes(returnRoute.routeConfig?.path)) {
      returnRoute = returnRoute.parent;
    }

    const url = returnRoute.snapshot.pathFromRoot.map((route) => route.url.map((u) => u.path)).flat();
    url[0] = `/${url[0]}`;
    this.returnToUrl$.next(url);
  }

  readonly returnToUrl$ = new BehaviorSubject(['']);
  readonly returnText$ = this.returnToText();

  returnToText(): Observable<string> {
    return this.store.type$.pipe(
      first(),
      map((requestType) => {
        switch (requestType) {
          case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
            return 'Apply for an emissions monitoring plan';
          case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
            return 'Review emissions monitoring plan application';
          case 'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW':
          case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW':
            return 'Emissions monitoring plan application sent to peer reviewer';
          case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW':
            return 'Peer review emissions monitoring plan application';
          case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED':
          case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED':
          case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED':
          case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED':
            return 'Submitted';
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED':
            return 'EMP CORSIA submitted';
          case 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED':
            return 'Approved';
          case 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN':
            return 'Deemed withdrawn';
          case 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS':
          case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS':
            return 'Emissions monitoring plan application returned to operator';
          case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT':
          case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
            return 'Amend your emissions monitoring plan';

          case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
          case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
          case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED':
            return 'Apply to vary your emissions monitoring plan';
          case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
          case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
            return 'Review emissions monitoring plan variation';
          case 'EMP_VARIATION_UKETS_WAIT_FOR_REVIEW':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW':
            return 'Emissions monitoring plan variation sent to regulator';
          case 'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW':
            return 'Emissions monitoring plan variation sent to peer reviewer';
          case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
            return 'Peer review emissions monitoring plan variation';
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
            return 'Vary the emissions monitoring plan';
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
            return 'Vary the emissions monitoring plan sent to peer reviewer';
          case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
          case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
            return 'Peer review vary the emissions monitoring plan';

          case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
          case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
            return 'Emissions report';
          case 'AVIATION_AER_UKETS_APPLICATION_REVIEW':
          case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW':
            return 'Review emissions report';
          case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT':
          case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT':
          case 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT':
          case 'AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT':
            return 'Verify emissions report';

          case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED':
          case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED':
            return 'Verify emissions report submitted';

          case 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED':
          case 'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED':
          case 'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER':
          case 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER':
            return 'Emissions report submitted';
          case 'AVIATION_AER_UKETS_APPLICATION_COMPLETED':
          case 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED':
            return 'Emissions report reviewed';
          case 'AVIATION_AER_UKETS_APPLICATION_REVIEW_SKIPPED':
          case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED':
            return 'Completed without review';

          case 'AVIATION_AER_UKETS_WAIT_FOR_AMENDS':
          case 'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS':
            return 'Emissions report returned to operator';
          case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
          case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
            return 'Amend your emissions report';
          case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER':
          case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER':
          case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED':
          case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED':
            return 'Changes submitted';

          case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
            return 'Determine aviation emissions report';
          case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
            return 'Peer review aviation emissions report determination';
          case 'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW':
            return 'Aviation emissions report determination sent to peer reviewer';

          case 'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED':
            return 'Apply to vary your emissions monitoring plan';
          case 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED':
          case 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED':
          case 'EMP_VARIATION_UKETS_APPLICATION_APPROVED':
            return 'Approved';
          case 'EMP_VARIATION_UKETS_APPLICATION_REJECTED':
            return 'Rejected';
          case 'EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN':
            return 'Deemed withdrawn';
          case 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED':
            return 'Approved';
          case 'EMP_VARIATION_CORSIA_APPLICATION_REJECTED':
            return 'Rejected';
          case 'EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN':
            return 'Deemed withdrawn';
          case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
          case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
            return 'Amend your emissions monitoring plan variation';
          case 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS':
          case 'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS':
            return 'Emissions monitoring plan variation returned to operator';

          case 'AVIATION_VIR_APPLICATION_SUBMIT':
          case 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS':
            return 'Verifier improvement report';
          case 'AVIATION_VIR_APPLICATION_SUBMITTED':
            return 'Verifier improvement report submitted';
          case 'AVIATION_VIR_APPLICATION_REVIEW':
            return 'Review verifier improvement report';
          case 'AVIATION_VIR_APPLICATION_REVIEWED':
            return 'Verifier improvement report decision submitted';
          case 'AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
            return 'Follow up response submitted';

          case 'AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT':
            return 'Non compliance task';
          case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
          case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
            return 'Initial penalty notice';
          case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
            return 'Peer review initial penalty';
          case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
          case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
            return 'Notice of intent';
          case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
            return 'Peer review notice of intent';
          case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
          case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
            return 'Penalty notice';
          case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
            return 'Peer review penalty notice';
          case 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION':
            return 'Final determination';
          case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT':
            return 'Calculate annual offsetting requirements';
          case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW':
            return 'Peer review calculate annual offsetting requirements';
          case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT':
            return 'Calculate 3-year period offsetting requirements';
          case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW':
            return 'Peer review 3-year period offsetting requirements';

          case 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT':
            return 'Estimate emissions';
          case 'AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW':
            return 'Aviation emissions report estimation sent to peer reviewer';
          case 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW':
            return 'Peer review emissions estimation';

          default: {
            throw new Error('[ReturnTo component]: Request task/action type is not handled');
          }
        }
      }),
    );
  }
}
