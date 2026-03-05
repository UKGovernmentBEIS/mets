import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { aerHeaderTaskMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerRequestMetadata } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';
import { getSectionsForTaskType } from '../../../../util';
import { ReportingObligationViewModel } from '../reporting-obligation.interface';
import { ReportingObligationFormProvider } from '../reporting-obligation-form.provider';
import { ReportingObligationFormComponent } from '../reporting-obligation-form/reporting-obligation-form.component';

@Component({
  selector: 'app-reporting-obligation-page',
  templateUrl: './reporting-obligation-page.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent, ReportingObligationFormComponent],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReportingObligationPageComponent {
  private store = inject(RequestTaskStore);
  private formProvider = inject<ReportingObligationFormProvider>(TASK_FORM_PROVIDER);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly aerHeaderTaskMap = aerHeaderTaskMap;

  reportingObligationForm = this.formProvider.form;

  vm$: Observable<ReportingObligationViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTask),
    this.store.pipe(aerQuery.selectIsCorsia),
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestTask, isCorsia, payload, requestInfo, isEditable]) => {
      return {
        heading: aerHeaderTaskMap['reportingObligation'],
        year: (requestInfo.requestMetadata as AviationAerRequestMetadata).year,
        isCorsia,
        requestTask,
        sections: getSectionsForTaskType(requestTask.type, requestInfo.type, payload),
        downloadUrl: `${this.store.aerDelegate.baseFileAttachmentDownloadUrl}/`,
        submitHidden: !isEditable,
      };
    }),
  );

  onSubmit() {
    if (this.reportingObligationForm.valid) {
      this.formProvider.form.updateValueAndValidity({ onlySelf: true, emitEvent: false });

      this.store.aerDelegate
        .saveAer({ reportingObligation: this.formProvider.getFormValue() }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.store.aerDelegate.setReportingObligation(this.formProvider.getFormValue());
          this.formProvider.form.value?.reportingObligationDetails?.supportingDocuments?.forEach((doc) => {
            this.store.aerDelegate.addAerAttachment({ [doc.uuid]: doc.file.name });
          });

          this.router.navigate(['summary'], { relativeTo: this.route });
        });
    }
  }
}
