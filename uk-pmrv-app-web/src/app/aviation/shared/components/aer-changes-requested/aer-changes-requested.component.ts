import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable } from 'rxjs';

import {
  aerCorsiaReviewGroupMap,
  aerReviewCorsiaHeaderTaskMap,
} from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import {
  AER_APPLICATION_TASKS,
  aerHeaderTaskMap,
  aerReviewGroupMap,
} from '@aviation/request-task/aer/shared/util/aer.util';
import { AerRequestTaskPayload, requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AerReviewDecision } from 'pmrv-api';

import { ReturnToLinkComponent } from '../return-to-link';
import { AER_CHANGES_REQUESTED_FORM, AerChangesRequestedFormProvider } from './aer-changes-requested-form.provider';

interface ViewModel {
  pageHeader: string;
  data: Array<{ [key: string]: AerReviewDecision }>;
  reviewAttachments: { [p: string]: string };
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
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestInfo, payload, isEditable]) => {
      const reviewGroupDecisions = (payload as AerRequestTaskPayload).reviewGroupDecisions;

      const transformedReviewGroupDecisions = CorsiaRequestTypes.includes(requestInfo.type)
        ? this.transformDataCorsia(reviewGroupDecisions)
        : this.transformData(reviewGroupDecisions);

      return {
        pageHeader: 'Changes requested by the regulator',
        data: transformedReviewGroupDecisions,
        reviewAttachments: (payload as AerRequestTaskPayload).reviewAttachments,
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

  private transformDataCorsia(changesRequired: AerRequestTaskPayload['reviewGroupDecisions']) {
    const aerReviewGroupMapEntries = Object.entries({
      ...aerCorsiaReviewGroupMap,
    });

    const sectionNameMapper = aerReviewGroupMapEntries
      .filter((group) => changesRequired[group[1]])
      .map((group) => ({
        sectionData: { ...changesRequired[group[1]] },
        sectionName: aerReviewCorsiaHeaderTaskMap[group[0]],
      }))
      .sort((a, b) => {
        const allTasks = AER_APPLICATION_TASKS.map((t) => t.tasks)
          .reduce((acc, tasks) => acc.concat(tasks), [])
          .map((task) => task.name);

        return (
          allTasks.indexOf(
            Object.keys(aerReviewCorsiaHeaderTaskMap).find(
              (key) => aerReviewCorsiaHeaderTaskMap[key] === a.sectionName,
            ),
          ) -
          allTasks.indexOf(
            Object.keys(aerReviewCorsiaHeaderTaskMap).find(
              (key) => aerReviewCorsiaHeaderTaskMap[key] === b.sectionName,
            ),
          )
        );
      });

    return sectionNameMapper;
  }

  getDownloadUrlFiles(
    files: string[],
    attachments: { [p: string]: string },
  ): { downloadUrl: string; fileName: string }[] {
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
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
    }
  }
}
