import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerReviewCorsiaHeaderTaskMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/shared/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { showReviewDecisionComponent } from '@aviation/request-task/util';
import { AerEmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-corsia/aer-emissions-reduction-claim-corsia-template/aer-emissions-reduction-claim-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationAerCorsiaEmissionsReductionClaim;
  showDecision: boolean;
  cefFiles: AttachedFile[];
  declarationFiles: AttachedFile[];
}

@Component({
  selector: 'app-emissions-reduction-claim',
  standalone: true,
  imports: [
    ReturnToLinkComponent,
    SharedModule,
    AerEmissionsReductionClaimCorsiaTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-aer-emissions-reduction-claim-corsia-template
        [emissionsReductionClaim]="vm.data"
        [cefFiles]="vm.cefFiles"
        [declarationFiles]="vm.declarationFiles"></app-aer-emissions-reduction-claim-corsia-template>
      <app-aviation-aer-review-decision-group
        *ngIf="vm.showDecision"
        taskKey="emissionsReductionClaim"></app-aviation-aer-review-decision-group>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerVerifyCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([type, aer, aerAttachments]) => {
      return {
        heading: aerReviewCorsiaHeaderTaskMap.emissionsReductionClaim,
        data: aer.emissionsReductionClaim,
        showDecision: showReviewDecisionComponent.includes(type),
        cefFiles:
          aer.emissionsReductionClaim?.emissionsReductionClaimDetails?.cefFiles?.map((id) => ({
            downloadUrl: `${
              (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).baseFileAttachmentDownloadUrl
            }/${id}`,
            fileName: aerAttachments[id],
          })) ?? [],
        declarationFiles:
          aer.emissionsReductionClaim?.emissionsReductionClaimDetails?.noDoubleCountingDeclarationFiles?.map((id) => ({
            downloadUrl: `${
              (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).baseFileAttachmentDownloadUrl
            }/${id}`,
            fileName: aerAttachments[id],
          })) ?? [],
      };
    }),
  );

  constructor(private store: RequestTaskStore) {}
}
