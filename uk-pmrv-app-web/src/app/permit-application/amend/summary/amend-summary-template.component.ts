import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { reviewGroupAllHeading } from '../../review/utils/review.global';
import { AmendGroup, amendGroupAllHeading, GroupKeyAndAmendDecision } from '../../shared/types/amend.global.type';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-amend-summary-template',
  templateUrl: './amend-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AmendSummaryTemplateComponent {
  returnTo: string = this.store.getApplyForHeader();
  section = this.route.snapshot.paramMap.get('section') as AmendGroup;
  reviewGroupDecisions$: Observable<GroupKeyAndAmendDecision[]> = this.store.getSectionGroupsWithAmendDecisionByGroup$(
    this.section,
  );

  heading = amendGroupAllHeading;
  groupHeading = reviewGroupAllHeading;

  constructor(readonly store: PermitApplicationStore<PermitApplicationState>, private readonly route: ActivatedRoute) {}
}
