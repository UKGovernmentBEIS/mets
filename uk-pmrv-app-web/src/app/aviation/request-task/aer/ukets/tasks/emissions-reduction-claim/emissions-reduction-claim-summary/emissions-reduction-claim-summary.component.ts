import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { EmissionsReductionClaimSummaryTemplateComponent } from '@aviation/shared/components/aer/emissions-reduction-claim-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerSaf, AviationAerSafPurchase } from 'pmrv-api';

import { AerReviewDecisionGroupComponent } from '../../../aer-review-decision-group/aer-review-decision-group.component';
import { aerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

interface ViewModel {
  data: AviationAerSaf;
  purchases: {
    purchase: AviationAerSafPurchase;
    files: { downloadUrl: string; fileName: string }[];
  }[];
  declarationFile: { downloadUrl: string; fileName: string };
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-aer-emissions-reduction-claim-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    EmissionsReductionClaimSummaryTemplateComponent,
    AerReviewDecisionGroupComponent,
  ],
  templateUrl: './emissions-reduction-claim-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimSummaryComponent implements OnInit, OnDestroy {
  form = this.formProvider.form;
  downloadUrl = `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('saf')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        data: {
          ...this.form.value,
          safDetails: {
            ...this.form.value.safDetails,
            noDoubleCountingDeclarationFile: this.form.value.safDetails?.noDoubleCountingDeclarationFile?.file.name,
          },
        },
        purchases:
          this.form.value.safDetails?.purchases?.map((item) => ({
            purchase: item,
            files:
              item?.evidenceFiles?.map((doc) => {
                return {
                  fileName: doc.file.name,
                  downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
                };
              }) ?? [],
          })) ?? [],
        declarationFile: {
          fileName: this.form.value.safDetails?.noDoubleCountingDeclarationFile?.file.name,
          downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${this.form.value.safDetails?.noDoubleCountingDeclarationFile?.uuid}`,
        },
        pageHeader: getSummaryHeaderForTaskType(type, 'saf'),
        isEditable,
        hideSubmit:
          !isEditable ||
          ['complete', 'cannot start yet'].includes(taskStatus) ||
          (this.form.value?.safDetails?.purchases && this.form.value?.safDetails?.purchases?.length == 0),
        showDecision: showReviewDecisionComponent.includes(type),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: aerEmissionsReductionClaimFormProvider,
    private store: RequestTaskStore,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    if (this.form?.valid) {
      this.store.aerDelegate
        .saveAer({ saf: this.formProvider.getFormValue() }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../'], { relativeTo: this.route });
        });
    }
  }
}
