import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable, startWith } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { SectionComponent } from '../shared/section/section.component';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { siteDiagramAddFormFactory } from './site-diagram-form.provider';

@Component({
  selector: 'app-site-diagram',
  templateUrl: './site-diagram.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [siteDiagramAddFormFactory],
})
export class SiteDiagramComponent extends SectionComponent implements PendingRequest {
  isFileUploaded$: Observable<boolean> = this.form.get('siteDiagrams').valueChanges.pipe(
    startWith(this.form.get('siteDiagrams').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit(): void {
    this.store
      .postTask(
        'siteDiagrams',
        this.form.value.siteDiagrams?.map((file) => file.uuid),
        true,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value.siteDiagrams?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
          },
        });

        this.navigateSubmitSection('summary', 'fuels');
      });
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }
}
