import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, startWith, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { reviewRequestTaskTypes } from '../../../shared/utils/permit';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { descriptionFormProvider } from './description-form.provider';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [descriptionFormProvider],
})
export class DescriptionComponent {
  isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) =>
          this.store.patchTask(
            data.taskKey,
            {
              ...this.form.value,
              files: this.form.value.files?.map((file) => file.uuid),
            },
            true,
            data.statusKey,
          ),
        ),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
        first(),
        tap((state) =>
          this.store.setState({
            ...state,
            permitAttachments: {
              ...state.permitAttachments,
              ...this.form.value.files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
            },
          }),
        ),
      )
      .subscribe((state) =>
        this.router.navigate(
          [reviewRequestTaskTypes.includes(state.requestTaskType) ? '../../review/fall-back' : 'summary'],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }
}
