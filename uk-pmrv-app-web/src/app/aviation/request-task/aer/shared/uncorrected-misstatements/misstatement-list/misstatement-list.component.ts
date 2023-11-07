import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { uncorrectedMisstatementsQuery } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { UncorrectedItemGroupComponent } from '@aviation/shared/components/aer-verify/uncorrected-item-group/uncorrected-item-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerUncorrectedMisstatements } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  uncorrectedMisstatements: AviationAerUncorrectedMisstatements;
}
@Component({
  selector: 'app-misstatement-list',
  templateUrl: './misstatement-list.component.html',
  standalone: true,
  providers: [DestroySubject],
  imports: [GovukComponentsModule, SharedModule, RouterModule, UncorrectedItemGroupComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementListComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(uncorrectedMisstatementsQuery.selectUncorrectedMisstatements),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([uncorrectedMisstatements, isEditable]) => ({
      isEditable,
      uncorrectedMisstatements: uncorrectedMisstatements,
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
