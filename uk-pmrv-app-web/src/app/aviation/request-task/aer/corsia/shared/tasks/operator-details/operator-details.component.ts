import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationCorsiaOperatorDetails, LimitedCompanyOrganisation } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationCorsiaOperatorDetails;
  showDecision: boolean;
  certificationFiles: AttachedFile[];
  evidenceFiles: AttachedFile[];
}

@Component({
  selector: 'app-operator-details',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    OperatorDetailsSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-operator-details-summary-template
        [data]="vm.data"
        [certificationFiles]="vm.certificationFiles"
        [evidenceFiles]="vm.evidenceFiles"
        [isCorsia]="true"></app-operator-details-summary-template>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="operatorDetails"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerVerifyCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([type, aer, aerAttachments]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.operatorDetails,
        data: aer.operatorDetails,
        showDecision: showReviewDecisionComponent.includes(type),
        certificationFiles:
          aer.operatorDetails?.airOperatingCertificate?.certificateFiles?.map((uuid) => {
            const file = aerAttachments[uuid];
            return {
              fileName: file,
              downloadUrl: `${
                (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).baseFileAttachmentDownloadUrl
              }/${uuid}`,
            };
          }) ?? [],
        evidenceFiles:
          (aer.operatorDetails?.organisationStructure as LimitedCompanyOrganisation)?.evidenceFiles?.map((uuid) => {
            const file = aerAttachments[uuid];
            return {
              fileName: file,
              downloadUrl: `${
                (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).baseFileAttachmentDownloadUrl
              }/${uuid}`,
            };
          }) ?? [],
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
