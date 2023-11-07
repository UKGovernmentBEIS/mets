import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { OperatorDetailsSummaryTemplateComponent } from '@aviation/shared/components/operator-details/operator-details-summary-template/operator-details-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';

import { AviationCorsiaOperatorDetails, LimitedCompanyOrganisation } from 'pmrv-api';

interface ViewModel {
  heading: string;
  data: AviationCorsiaOperatorDetails;
  certificationFiles: AttachedFile[];
  evidenceFiles: AttachedFile[];
}

@Component({
  selector: 'app-operator-details',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, OperatorDetailsSummaryTemplateComponent],
  template: `
    <ng-container *ngIf="vm$ | async as vm">
      <app-page-heading>{{ vm.heading }}</app-page-heading>
      <app-operator-details-summary-template
        [data]="vm.data"
        [certificationFiles]="vm.certificationFiles"
        [evidenceFiles]="vm.evidenceFiles"
        [isCorsia]="true"
      ></app-operator-details-summary-template>
    </ng-container>
    <app-return-to-link></app-return-to-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorDetailsComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(aerVerifyCorsiaQuery.selectAer),
    this.store.pipe(aerVerifyCorsiaQuery.selectAerAttachments),
  ]).pipe(
    map(([aer, aerAttachments]) => {
      return {
        heading: aerHeaderTaskMap['operatorDetails'],
        data: aer.operatorDetails,
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
