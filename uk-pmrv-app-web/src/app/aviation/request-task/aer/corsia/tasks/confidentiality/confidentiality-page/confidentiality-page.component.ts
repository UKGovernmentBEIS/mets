import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { requestTaskQuery, RequestTaskStore } from '../../../../../store';
import { getSectionsForTaskType } from '../../../../../util';
import { ConfidentialityFormProvider, ConfidentialityViewModel } from '../confidentiality-form.provider';
import { ConfidentialityFormComponent } from '../confidentiality-form/confidentiality-form.component';

@Component({
  selector: 'app-confidentiality-page',
  templateUrl: './confidentiality-page.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent, ConfidentialityFormComponent],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfidentialityPageComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<ConfidentialityFormProvider>(TASK_FORM_PROVIDER);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly aerHeaderTaskMap = aerHeaderTaskMap;

  confidentialityForm = this.formProvider.form;

  vm$: Observable<ConfidentialityViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTask),
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
  ]).pipe(
    map(([requestTask, payload, isEditable, requestInfo]) => {
      return {
        heading: aerHeaderTaskMap['confidentiality'],
        requestTask,
        sections: getSectionsForTaskType(requestTask.type, requestInfo.type, payload),
        downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  onSubmit() {
    if (this.confidentialityForm.valid) {
      this.formProvider.form.updateValueAndValidity({ onlySelf: true, emitEvent: false });
      const confidentiality = this.formProvider.getFormValue();
      this.store.aerDelegate
        .saveAer({ confidentiality: confidentiality }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          (this.store.aerDelegate as AerCorsiaStoreDelegate).setConfidentiality(confidentiality);
          this.formProvider.form.value?.totalEmissionsDocuments?.forEach((doc) => {
            this.store.aerDelegate.addAerAttachment({ [doc.uuid]: doc.file.name });
          });
          this.formProvider.form.value?.aggregatedStatePairDataDocuments?.forEach((doc) => {
            this.store.aerDelegate.addAerAttachment({ [doc.uuid]: doc.file.name });
          });
          this.router.navigate(['summary'], { relativeTo: this.route });
        });
    }
  }
}
