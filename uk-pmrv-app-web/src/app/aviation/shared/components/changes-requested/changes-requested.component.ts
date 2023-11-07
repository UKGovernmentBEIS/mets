import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, tap } from 'rxjs';

import { empHeaderTaskMap, empReviewGroupMap } from '@aviation/request-task/emp/shared/util/emp.util';
import {
  EmpRequestTaskPayload,
  requestTaskQuery,
  RequestTaskState,
  RequestTaskStore,
} from '@aviation/request-task/store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { EmpIssuanceReviewDecision } from 'pmrv-api';

import { ReturnToLinkComponent } from '../return-to-link';
import { CHANGES_REQUESTED_FORM, ChangesRequestedFormProvider } from './changes-requested-form.provider';

interface ViewModel {
  pageHeader: string;
  data: Array<{ [key: string]: EmpIssuanceReviewDecision }>;
  isEditable: boolean;
}

@Component({
  selector: 'app-changes-requested',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [ChangesRequestedFormProvider],
  templateUrl: './changes-requested.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangesRequestedComponent {
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskPayload),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([payload, isEditable]) => {
      const reviewGroupDecisions = (payload as EmpRequestTaskPayload).reviewGroupDecisions;
      const empVariationDetailsReviewDecision = (payload as EmpRequestTaskPayload).empVariationDetailsReviewDecision;
      const transformedReviewGroupDecisions =
        empVariationDetailsReviewDecision && (payload as EmpRequestTaskPayload).empVariationDetailsReviewCompleted
          ? this.transformData({
              ...reviewGroupDecisions,
              EMP_VARIATION_DETAILS: empVariationDetailsReviewDecision,
            } as EmpRequestTaskPayload['reviewGroupDecisions'])
          : this.transformData(reviewGroupDecisions);

      return {
        pageHeader: 'Changes requested by the regulator',
        data: transformedReviewGroupDecisions,
        isEditable,
      };
    }),
  );

  constructor(
    protected readonly store: RequestTaskStore,
    private readonly fb: FormBuilder,
    public pendingRequestService: PendingRequestService,
    public router: Router,
    public route: ActivatedRoute,
    @Inject(CHANGES_REQUESTED_FORM) readonly form: UntypedFormGroup,
  ) {}

  private transformData(changesRequired: EmpRequestTaskPayload['reviewGroupDecisions']) {
    const empReviewGroupMapEntries = Object.entries({
      empVariationDetails: 'EMP_VARIATION_DETAILS',
      ...empReviewGroupMap,
    });

    const sectionNameMapper = empReviewGroupMapEntries
      .filter((group) => changesRequired[group[1]])
      .map((group) => ({
        sectionData: { ...changesRequired[group[1]] },
        sectionName: empHeaderTaskMap[group[0]],
      }));

    return sectionNameMapper;
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const attachments = this.store.empDelegate.payload.reviewAttachments;

    return (
      files?.map((file) => ({
        fileName: attachments[file],
        downloadUrl: this.store.empDelegate.baseFileAttachmentDownloadUrl + `/${file}`,
      })) ?? []
    );
  }

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.store.empDelegate
        .saveEmp({}, 'complete')
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
                    empSectionsCompleted: {
                      ...(state.requestTaskItem.requestTask.payload as EmpRequestTaskPayload).empSectionsCompleted,
                      changesRequested: this.form.value.changes,
                    },
                  },
                },
              },
            } as RequestTaskState);
          }),
        )
        .subscribe(() => this.router.navigate(['../../../'], { relativeTo: this.route }));
    }
  }
}
