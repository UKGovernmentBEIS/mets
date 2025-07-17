import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';
import { typeOptions } from '@shared/components/source-streams/source-stream-options';

import { SourceStream } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { sourceStreamFormProvider } from './source-stream-form.provider';

@Component({
  selector: 'app-source-streams-details',
  templateUrl: './source-streams-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [sourceStreamFormProvider],
})
export class SourceStreamDetailsComponent {
  isEditing$ = this.store
    .getTask('sourceStreams')
    .pipe(map((sourceStreams) => sourceStreams.some((item) => item.id === this.form.get('id').value)));

  typeOptions = typeOptions;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  requestTaskType$ = this.store.pipe(map((response) => reviewRequestTaskTypes.includes(response.requestTaskType)));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.store
      .findTask<SourceStream[]>('sourceStreams')
      .pipe(
        first(),
        switchMap((sourceStreams) =>
          this.store.postTask(
            'sourceStreams',
            sourceStreams.some((item) => item.id === this.form.value.id)
              ? sourceStreams.map((item) => (item.id === this.form.value.id ? this.form.value : item))
              : [...sourceStreams, this.form.value],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
