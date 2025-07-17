import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerVerificationReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-verification-review-decision-group/aer-verification-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { GeneralInformationCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/general-information-corsia-template/general-information-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaGeneralInformation } from 'pmrv-api';

interface ViewModel {
  heading: string;
  generalInformation: AviationAerCorsiaGeneralInformation;
  showDecision: boolean;
}

@Component({
  selector: 'app-general-information',
  standalone: true,
  imports: [
    SharedModule,
    ReturnToLinkComponent,
    GeneralInformationCorsiaTemplateComponent,
    AerVerificationReviewDecisionGroupComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-general-information-corsia-template [data]="vm.generalInformation"></app-general-information-corsia-template>
      <app-aviation-aer-verification-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="generalInformation"></app-aviation-aer-verification-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
})
export class GeneralInformationComponent {
  private store = inject(RequestTaskStore);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectGeneralInformation),
  ]).pipe(
    map(([type, generalInformation]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.generalInformation,
        generalInformation: generalInformation,
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );
}
