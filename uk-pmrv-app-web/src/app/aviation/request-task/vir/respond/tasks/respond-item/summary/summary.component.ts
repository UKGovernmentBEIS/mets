import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RespondItemFormProvider } from '@aviation/request-task/vir/respond/tasks/respond-item/respond-item-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import {
  OperatorImprovementFollowUpResponse,
  OperatorImprovementResponse,
  RegulatorImprovementResponse,
} from 'pmrv-api';

interface ViewModel {
  heading: string;
  verificationDataItem: VerificationDataItem;
  operatorImprovementResponse: OperatorImprovementResponse;
  documentFiles: AttachedFile[];
  regulatorImprovementResponse: RegulatorImprovementResponse;
  operatorImprovementFollowUpResponse: OperatorImprovementFollowUpResponse;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-summary',
  standalone: true,
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [VirSharedModule, SharedModule, ReturnToLinkComponent],
})
export class SummaryComponent {
  private verificationDataItem = this.route.snapshot.data.verificationDataItem as VerificationDataItem;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(virQuery.selectOperatorImprovementResponses),
    this.store.pipe(virQuery.selectRespondRegulatorImprovementResponses),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(virQuery.selectRespondToRegulatorCommentsSectionsCompleted),
    this.store.pipe(virQuery.selectAttachments),
  ]).pipe(
    map(([operatorImprovementResponses, regulatorImprovementResponses, isEditable, sectionsCompleted, attachments]) => {
      return {
        heading: 'Check your answers',
        verificationDataItem: this.verificationDataItem,
        operatorImprovementResponse: operatorImprovementResponses[this.verificationDataItem.reference],
        documentFiles:
          operatorImprovementResponses[this.verificationDataItem.reference].files?.map((uuid) => {
            return {
              fileName: attachments[uuid],
              downloadUrl: `${this.store.virDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
            };
          }) ?? [],
        regulatorImprovementResponse: regulatorImprovementResponses[this.verificationDataItem.reference],
        operatorImprovementFollowUpResponse: this.formProvider.getFormValue(),
        isEditable: isEditable,
        hideSubmit: !isEditable || sectionsCompleted[this.verificationDataItem.reference],
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: RespondItemFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.virDelegate
      .saveRespondVir(this.formProvider.getFormValue(), this.verificationDataItem.reference, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['../../../..'], {
          relativeTo: this.route,
        });
      });
  }
}
