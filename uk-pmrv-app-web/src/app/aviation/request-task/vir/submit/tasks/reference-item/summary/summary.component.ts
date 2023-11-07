import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AttachedFile } from '@shared/types/attached-file.type';
import { VerificationDataItem } from '@shared/vir-shared/types/verification-data-item.type';
import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';

import { OperatorImprovementResponse } from 'pmrv-api';

interface ViewModel {
  heading: string;
  verificationDataItem: VerificationDataItem;
  operatorImprovementResponse: OperatorImprovementResponse;
  documentFiles: AttachedFile[];
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
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(virQuery.selectSectionsCompleted),
    this.store.pipe(virQuery.selectAttachments),
  ]).pipe(
    map(([isEditable, sectionsCompleted, attachments]) => {
      return {
        heading: 'Check your answers',
        verificationDataItem: this.verificationDataItem,
        operatorImprovementResponse: this.formProvider.getFormValue(),
        documentFiles:
          this.formProvider.getFormValue().files?.map((uuid) => {
            return {
              fileName: attachments[uuid],
              downloadUrl: `${this.store.virDelegate.baseFileAttachmentDownloadUrl}/${uuid}`,
            };
          }) ?? [],
        isEditable: isEditable,
        hideSubmit: !isEditable || sectionsCompleted[this.verificationDataItem.reference],
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: ReferenceItemFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store.virDelegate
      .saveVir(this.formProvider.getFormValue(), this.verificationDataItem.reference, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() =>
        this.router.navigate(['../../../..'], {
          relativeTo: this.route,
        }),
      );
  }
}
