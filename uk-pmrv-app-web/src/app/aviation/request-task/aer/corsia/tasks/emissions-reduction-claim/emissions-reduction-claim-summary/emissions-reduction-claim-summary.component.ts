import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { AerReviewDecisionGroupComponent } from '@aviation/request-task/aer/ukets/aer-review-decision-group/aer-review-decision-group.component';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType, showReviewDecisionComponent } from '@aviation/request-task/util';
import { AerEmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-corsia/aer-emissions-reduction-claim-corsia-template/aer-emissions-reduction-claim-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

interface ViewModel {
  hasDocuments: boolean;
  cefFiles: { downloadUrl: string; fileName: string }[];
  noDoubleCountingDeclarationFiles: { downloadUrl: string; fileName: string }[];

  data: AviationAerCorsiaEmissionsReductionClaim;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
  showDecision: boolean;
}

@Component({
  selector: 'app-emissions-reduction-claim-summary',
  standalone: true,
  imports: [
    SharedModule,
    GovukComponentsModule,
    NgFor,
    NgIf,
    RouterLinkWithHref,
    ReturnToLinkComponent,
    AerReviewDecisionGroupComponent,
    RouterLink,
    AerEmissionsReductionClaimCorsiaTemplateComponent,
  ],
  templateUrl: './emissions-reduction-claim-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimSummaryComponent {
  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('emissionsReductionClaim')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        data: this.form.valid ? this.form.value : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'emissionsReductionClaim'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
        showDecision: showReviewDecisionComponent.includes(type),
        hasDocuments: this.form.value.exist,
        cefFiles:
          this.form.value?.emissionsReductionClaimDetails?.cefFiles?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
        noDoubleCountingDeclarationFiles:
          this.form.value?.emissionsReductionClaimDetails?.noDoubleCountingDeclarationFiles?.map((doc) => {
            return {
              fileName: doc.file.name,
              downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/${doc.uuid}`,
            };
          }) ?? [],
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: EmissionsReductionClaimFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    if (this.form?.valid) {
      this.store.aerDelegate
        .saveAer({ emissionsReductionClaim: this.formProvider.getFormValue() }, 'complete')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../'], { relativeTo: this.route });
        });
    }
  }
}
