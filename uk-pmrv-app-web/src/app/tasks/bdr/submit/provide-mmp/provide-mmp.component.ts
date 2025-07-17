import { ChangeDetectionStrategy, Component, Inject, Signal } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { provideMMPFormProvider } from './provide-mmp-form.provider';

@Component({
  selector: 'app-provide-mmp',
  templateUrl: './provide-mmp.component.html',
  providers: [provideMMPFormProvider],
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideMmpComponent implements PendingRequest {
  isEditable$ = this.bdrService.isEditable$;
  bdrPayload: Signal<BDRApplicationSubmitRequestTaskPayload> = this.bdrService.payload;

  constructor(
    @Inject(BDR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const nextRoute = '../summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      const payload = this.bdrPayload();
      this.bdrService
        .postTaskSave(
          {
            hasMmp: this.form.value.hasMmp,
            mmpFiles: this.form.controls?.mmpFiles?.value?.map((file) => file.uuid),
          },
          {
            ...payload?.bdrAttachments,
            ...this.getBdrAttachments(),
          },
          false,
          'baseline',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }

  getDownloadUrl() {
    return this.bdrService.getBaseFileDownloadUrl();
  }

  private getBdrAttachments() {
    return this.form.controls?.mmpFiles.value?.reduce((acc, file) => ({ ...acc, [file.uuid]: file.file.name }), {});
  }
}
