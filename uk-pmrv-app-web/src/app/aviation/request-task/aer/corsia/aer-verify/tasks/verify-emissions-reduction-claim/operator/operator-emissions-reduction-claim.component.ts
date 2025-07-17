import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { AerEmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-corsia/aer-emissions-reduction-claim-corsia-template/aer-emissions-reduction-claim-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

interface ViewModel {
  pageHeader: string;
  emissionsReductionClaim: AviationAerCorsiaEmissionsReductionClaim;
  cefFiles: AttachedFile[];
  declarationFiles: AttachedFile[];
}

@Component({
  selector: 'app-operator-emissions-reduction-claim',
  standalone: true,
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.pageHeader }}</app-page-heading>
      <app-aer-emissions-reduction-claim-corsia-template
        [emissionsReductionClaim]="vm.emissionsReductionClaim"
        [cefFiles]="vm.cefFiles"
        [declarationFiles]="vm.declarationFiles"></app-aer-emissions-reduction-claim-corsia-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, AerEmissionsReductionClaimCorsiaTemplateComponent],
})
export class OperatorEmissionsReductionClaimComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerVerifyCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([aer, aerAttachments]) => {
      return {
        pageHeader: `Operator's emission reduction claim`,
        emissionsReductionClaim: aer.emissionsReductionClaim,
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
