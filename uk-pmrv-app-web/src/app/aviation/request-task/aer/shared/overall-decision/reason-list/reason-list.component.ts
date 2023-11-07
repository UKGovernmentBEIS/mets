import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { overallDecisionQuery } from '@aviation/request-task/aer/shared/overall-decision/overall-decision.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerVerifiedSatisfactoryWithCommentsDecision } from 'pmrv-api';
interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  reasons: string[];
}
@Component({
  selector: 'app-reason-list',
  templateUrl: './reason-list.component.html',
  standalone: true,
  providers: [DestroySubject],
  imports: [GovukComponentsModule, SharedModule, RouterModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonListComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(overallDecisionQuery.selectOverallDecision),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([overallDecision, isEditable]) => ({
      pageHeader: 'What is your assessment of this report?',
      isEditable,
      reasons: (overallDecision as AviationAerVerifiedSatisfactoryWithCommentsDecision)?.reasons,
    })),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue() {
    return this.router.navigate(['../summary'], { relativeTo: this.route, queryParams: { change: true } });
  }
}
