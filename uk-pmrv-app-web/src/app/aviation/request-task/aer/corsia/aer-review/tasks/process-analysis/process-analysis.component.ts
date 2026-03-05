import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { ProcessAnalysisCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/process-analysis-corsia-template/process-analysis-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaProcessAnalysis } from 'pmrv-api';

interface ViewModel {
  heading: string;
  processAnalysis: AviationAerCorsiaProcessAnalysis;
  showDecision: boolean;
}

@Component({
  selector: 'app-process-analysis',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    ProcessAnalysisCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-process-analysis-corsia-template [data]="vm.processAnalysis"></app-process-analysis-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="processAnalysis"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
})
export class ProcessAnalysisComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectProcessAnalysis),
  ]).pipe(
    map(([type, processAnalysis]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.processAnalysis,
        processAnalysis: processAnalysis,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
