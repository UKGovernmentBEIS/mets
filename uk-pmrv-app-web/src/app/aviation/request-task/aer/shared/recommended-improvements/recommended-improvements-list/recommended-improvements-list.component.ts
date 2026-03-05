import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { recommendedImprovementsQuery } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { RecommendedImprovementsGroupComponent } from '@aviation/shared/components/aer-verify/recommended-improvements-group/recommended-improvements-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerRecommendedImprovements } from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  recommendedImprovements: AviationAerRecommendedImprovements;
}
@Component({
  selector: 'app-recommended-improvements-list',
  templateUrl: './recommended-improvements-list.component.html',
  standalone: true,
  providers: [DestroySubject],
  imports: [
    GovukComponentsModule,
    SharedModule,
    RouterModule,
    RecommendedImprovementsGroupComponent,
    ReturnToLinkComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsListComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(recommendedImprovementsQuery.selectRecommendedImprovements),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([recommendedImprovements, isEditable]) => ({
      isEditable,
      recommendedImprovements: recommendedImprovements,
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
