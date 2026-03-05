import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, Observable, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { ItemDTO, RequestActionInfoDTO, RequestTaskDTO, RequestTaskItemDTO } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../store';
import { TASK_FORM_PROVIDER } from '../../task-form.provider';
import { getRequestTaskHeaderForTaskType, getSectionsForTaskType } from '../../util';
import { AccountClosureFormModel, accountClosureFormProvider } from '../account-closure-form.provider';

interface ViewModel {
  form: FormGroup<AccountClosureFormModel>;
  requestTask: RequestTaskDTO;
  timeline: RequestActionInfoDTO[];
  relatedTasks: ItemDTO[];
  isAssignable$: Observable<boolean>;
  taskId$: Observable<number>;
  hasRelatedActions$: Observable<boolean>;
  relatedActions$: Observable<RequestTaskItemDTO['allowedRequestTaskActions']>;
}

@Component({
  selector: 'app-account-closure-reason',
  standalone: true,
  imports: [ReactiveFormsModule, SharedModule, RouterModule],
  providers: [accountClosureFormProvider],
  templateUrl: './account-closure-reason.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountClosureReasonComponent {
  private store = inject(RequestTaskStore);
  private form = inject<FormGroup>(TASK_FORM_PROVIDER);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTask),
    this.store.pipe(requestTaskQuery.selectTimeline),
    this.store.pipe(requestTaskQuery.selectRelatedTasks),
    this.store.pipe(requestTaskQuery.selectUserAssignCapable),
    this.store.pipe(requestTaskQuery.selectRelatedActions),
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
  ]).pipe(
    map(([requestTask, timeline, relatedTasks, capableToAssign, relatedActions, payload, requestInfo]) => {
      return {
        header: getRequestTaskHeaderForTaskType(requestTask.type, requestInfo.requestMetadata),
        showDeadlineMessage: false,
        requestTask,
        timeline,
        relatedTasks,
        sections: getSectionsForTaskType(requestTask.type, requestInfo.type, payload),
        isAssignable$: of(requestTask.assignable),
        taskId$: of(requestTask.id),
        hasRelatedActions$: of(relatedActions?.length > 0 || (capableToAssign && requestTask.assignable)),
        relatedActions$: of(relatedActions),
        form: this.form,
      };
    }),
  );

  onContinue() {
    this.store.accountClosureDelegate
      .saveAccountClosure({ aviationAccountClosure: { reason: this.form.value.reason } })
      .subscribe(() => {
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
