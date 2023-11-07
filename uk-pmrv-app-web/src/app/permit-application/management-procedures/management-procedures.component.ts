import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, pluck, switchMap } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { ManagementProceduresDefinitionData } from './management-procedures.interface';
import { managementProceduresFormProvider } from './management-procedures-form.provider';

@Component({
  selector: 'app-management-procedures',
  templateUrl: './management-procedures.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [managementProceduresFormProvider],
})
export class ManagementProceduresComponent extends SectionComponent {
  permitTask$ = this.route.data.pipe(pluck<ManagementProceduresDefinitionData, 'permitTask'>('permitTask'));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    const file = this.form.value.diagramAttachmentId;

    this.permitTask$
      .pipe(
        first(),
        switchMap((permitTask) =>
          this.store.postTask(
            permitTask,
            {
              ...this.form.value,
              diagramAttachmentId: file?.uuid,
            },
            true,
          ),
        ),
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        if (file) {
          this.store.setState({
            ...this.store.getState(),
            permitAttachments: { ...this.store.getState().permitAttachments, [file.uuid]: file.file.name },
          });
        }
        this.navigateSubmitSection('summary', 'management-procedures');
      });
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['..', 'file-download', 'attachment', uuid];
  }
}
