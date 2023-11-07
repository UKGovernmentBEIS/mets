import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { EmissionsReductionClaimListTemplateComponent } from '@aviation/shared/components/aer/emissions-reduction-claim-list-template/emissions-reduction-claim-list-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { aerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-emissions-reduction-claim-list',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterModule, EmissionsReductionClaimListTemplateComponent],
  templateUrl: './emissions-reduction-claim-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimListComponent implements OnInit, OnDestroy {
  purchases = this.formProvider.safPurchases.value.map((item) => ({
    purchase: item,
    files:
      item?.evidenceFiles?.map((doc) => {
        return {
          fileName: doc.file.name,
          downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
        };
      }) ?? [],
  }));

  constructor(
    private store: RequestTaskStore,
    private router: Router,
    private route: ActivatedRoute,
    private backLinkService: BackLinkService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: aerEmissionsReductionClaimFormProvider,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  addAnotherBatch() {
    this.router.navigate(['../', 'add-batch-item'], { relativeTo: this.route });
  }

  onContinue() {
    this.router.navigate(['../', 'declaration'], { relativeTo: this.route });
  }
}
