import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaUncorrectedNonConformities } from 'pmrv-api';

interface ViewModel {
  heading: string;
  uncorrectedNonConformities: AviationAerCorsiaUncorrectedNonConformities;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-summary',
  standalone: true,
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, RouterLink, UncorrectedItemGroupComponent],
})
export class SummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyCorsiaQuery.selectUncorrectedNonConformities),
    this.store.pipe(aerVerifyCorsiaQuery.selectStatusForTask('uncorrectedNonConformities')),
  ]).pipe(
    map(([isEditable, uncorrectedNonConformities, taskStatus]) => {
      return {
        heading: 'Check your answers',
        uncorrectedNonConformities: uncorrectedNonConformities,
        isEditable: isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          (uncorrectedNonConformities?.existUncorrectedNonConformities === true &&
            (!uncorrectedNonConformities?.uncorrectedNonConformities ||
              uncorrectedNonConformities?.uncorrectedNonConformities.length === 0)),
      };
    }),
  );

  constructor(
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store
      .pipe(first(), aerVerifyCorsiaQuery.selectUncorrectedNonConformities)
      .pipe(
        switchMap((uncorrectedNonConformities) => {
          return (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).saveAerVerify(
            { uncorrectedNonConformities: uncorrectedNonConformities },
            'complete',
          );
        }),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route }));
  }
}
