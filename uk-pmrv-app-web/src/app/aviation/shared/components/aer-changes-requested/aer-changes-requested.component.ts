import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, tap } from 'rxjs';

import {
  AER_APPLICATION_TASKS,
  aerHeaderTaskMap,
  aerReviewGroupMap,
} from '@aviation/request-task/aer/shared/util/aer.util';
import {
  AerRequestTaskPayload,
  requestTaskQuery,
  RequestTaskState,
  RequestTaskStore,
} from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AerReviewDecision } from 'pmrv-api';

import { ReturnToLinkComponent } from '../return-to-link';
import { AER_CHANGES_REQUESTED_FORM, AerChangesRequestedFormProvider } from './aer-changes-requested-form.provider';

interface ViewModel {
  pageHeader: string;
  data: Array<{ [key: string]: AerReviewDecision }>;
  isEditable: boolean;
}

@Component({
  selector: 'app-aer-changes-requested',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [AerChangesRequestedFormProvider],
  templateUrl: './aer-changes-requested.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AerChangesRequestedComponent {
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([payload, isEditable]) => {
      const reviewGroupDecisions = (payload as AerRequestTaskPayload).reviewGroupDecisions;
      const transformedReviewGroupDecisions = this.transformData(reviewGroupDecisions);

      return {
        pageHeader: 'Changes requested by the regulator',
        data: transformedReviewGroupDecisions,
        isEditable,
      };
    }),
  );

  constructor(
    protected readonly store: RequestTaskStore,
    public pendingRequestService: PendingRequestService,
    public router: Router,
    public route: ActivatedRoute,
    @Inject(AER_CHANGES_REQUESTED_FORM) readonly form: UntypedFormGroup,
  ) {}

  private transformData(changesRequired: AerRequestTaskPayload['reviewGroupDecisions']) {
    const aerReviewGroupMapEntries = Object.entries({
      ...aerReviewGroupMap,
    });

    const sectionNameMapper = aerReviewGroupMapEntries
      .filter((group) => changesRequired[group[1]])
      .map((group) => ({
        sectionData: { ...changesRequired[group[1]] },
        sectionName: aerHeaderTaskMap[group[0]],
      }))
      .sort((a, b) => {
        const allTasks = AER_APPLICATION_TASKS.map((t) => t.tasks)
          .reduce((acc, tasks) => acc.concat(tasks), [])
          .map((task) => task.name);
        return (
          allTasks.indexOf(Object.keys(aerHeaderTaskMap).find((key) => aerHeaderTaskMap[key] === a.sectionName)) -
          allTasks.indexOf(Object.keys(aerHeaderTaskMap).find((key) => aerHeaderTaskMap[key] === b.sectionName))
        );
      });

    return sectionNameMapper;
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const attachments = (this.store.aerDelegate.payload as AerRequestTaskPayload).reviewAttachments;

    return (
      files?.map((file) => ({
        fileName: attachments[file],
        downloadUrl: this.store.aerDelegate.baseFileAttachmentDownloadUrl + `/${file}`,
      })) ?? []
    );
  }

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.store.aerDelegate
        .saveAer({}, 'complete')
        .pipe(
          this.pendingRequestService.trackRequest(),
          tap(() => {
            const state = this.store.getState();

            this.store.setState({
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...state.requestTaskItem.requestTask.payload,
                    aerSectionsCompleted: {
                      ...(state.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aerSectionsCompleted,
                      changesRequested: this.form.value.changes,
                    },
                  },
                },
              },
            } as RequestTaskState);
          }),
        )
        .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
    }
  }
}
