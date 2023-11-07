import { ChangeDetectionStrategy, Component, Inject, InjectionToken, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import {
  aerEmissionsReductionClaimFormProvider,
  AviationAerSafPurchaseType,
} from '../emissions-reduction-claim-form.provider';
import { aerEmissionsReductionClaimBatchItemFormProvider } from './emissions-reduction-claim-add-batch-item-form.provider';

export const AER_BATCH_ITEM_FORM = new InjectionToken<{ form: AviationAerSafPurchaseType; reset: () => void }>(
  'Batch Item form',
);

@Component({
  selector: 'app-emissions-reduction-claim-add-batch-item',
  standalone: true,
  providers: [
    {
      provide: AER_BATCH_ITEM_FORM,
      useClass: aerEmissionsReductionClaimBatchItemFormProvider,
    },
  ],
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './emissions-reduction-claim-add-batch-item.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimAddBatchItemComponent implements OnInit, OnDestroy {
  downloadUrl = `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`;

  form = this.formProviderBatchItem.form;

  private previousRouteSegments =
    this.router.getCurrentNavigation()?.previousNavigation?.initialUrl?.root.children.primary.segments;
  private lastSegment;

  constructor(
    private store: RequestTaskStore,
    private router: Router,
    private route: ActivatedRoute,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: aerEmissionsReductionClaimFormProvider,
    @Inject(AER_BATCH_ITEM_FORM)
    protected readonly formProviderBatchItem: aerEmissionsReductionClaimBatchItemFormProvider,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.lastSegment =
      this.previousRouteSegments?.length > 0
        ? this.previousRouteSegments[this.previousRouteSegments.length - 1].path
        : null;
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  get editIndex(): number | null {
    const idx = this.route.snapshot.queryParamMap.get('batchIndex');
    if (!idx) return null;
    return Number(idx);
  }

  onSubmit() {
    if (typeof this.editIndex === 'number') {
      this.formProvider.safPurchases.at(this.editIndex).patchValue({
        ...this.form.value,
        evidenceFiles: [...this.form.value.evidenceFiles],
      });

      const evidenceFormArray = (this.formProvider.safPurchases.at(this.editIndex) as FormGroup).get(
        'evidenceFiles',
      ) as FormArray;
      evidenceFormArray.clear();

      this.form.value.evidenceFiles.forEach((ef, efIndex) =>
        (this.formProvider.safPurchases.at(this.editIndex).get('evidenceFiles') as FormArray).push(
          new FormControl(this.form.value.evidenceFiles[efIndex]),
        ),
      );
    } else {
      this.formProvider.safPurchases.push(this.form);
    }

    const value = this.formProvider.getFormValue();

    this.store.aerDelegate
      .saveAer({ saf: { ...value } }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setSaf(value);

        this.form.value?.evidenceFiles?.forEach((doc) => {
          this.store.aerDelegate.addAerAttachment({ [doc.uuid]: doc.file.name });
        });

        this.formProvider.setFormValue(value);

        const path = this.lastSegment !== 'summary' ? 'list' : 'summary';
        this.router.navigate(['../', path], { relativeTo: this.route });
      });
  }
}
