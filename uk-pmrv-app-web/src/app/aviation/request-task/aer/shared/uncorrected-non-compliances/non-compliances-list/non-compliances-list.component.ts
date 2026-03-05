import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { uncorrectedNonCompliancesQuery } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedNonCompliances } from 'pmrv-api';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  uncorrectedNonCompliances: AviationAerUncorrectedNonCompliances;
}
@Component({
  selector: 'app-non-compliances-list',
  templateUrl: './non-compliances-list.component.html',
  standalone: true,
  providers: [DestroySubject],
  imports: [GovukComponentsModule, SharedModule, RouterModule, UncorrectedItemGroupComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesListComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerQuery.selectIsCorsia),
    this.store.pipe(uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([isCorsia, uncorrectedNonCompliances, isEditable]) => ({
      isEditable,
      pageHeader: isCorsia
        ? 'Non-compliances with the Air Navigation Order'
        : 'Non-compliances with the monitoring and reporting regulations',
      uncorrectedNonCompliances: uncorrectedNonCompliances,
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
