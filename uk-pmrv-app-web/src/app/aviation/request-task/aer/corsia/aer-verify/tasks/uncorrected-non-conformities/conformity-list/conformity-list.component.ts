import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaUncorrectedNonConformities } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  uncorrectedNonConformities: AviationAerCorsiaUncorrectedNonConformities;
}

@Component({
  selector: 'app-conformity-list',
  templateUrl: './conformity-list.component.html',
  standalone: true,
  providers: [DestroySubject],
  imports: [SharedModule, RouterModule, UncorrectedItemGroupComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConformityListComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerVerifyCorsiaQuery.selectUncorrectedNonConformities),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([uncorrectedNonConformities, isEditable]) => ({
      isEditable,
      uncorrectedNonConformities: uncorrectedNonConformities,
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
