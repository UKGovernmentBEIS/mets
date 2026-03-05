import { ChangeDetectionStrategy, Component, computed, Signal, signal, WritableSignal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { InspectionType } from '@tasks/inspection/shared/inspection';
import { InspectionTaskComponent } from '@tasks/inspection/shared/inspection-task/inspection-task.component';

import { InstallationInspectionOperatorRespondRequestTaskPayload, RequestInfoDTO } from 'pmrv-api';

interface ViewModel {
  competentAuthority: RequestInfoDTO['competentAuthority'];
  isEditable: boolean;
  isSubmitted: boolean;
  requestId: RequestInfoDTO['id'];
}

@Component({
  selector: 'app-follow-up-respond-send-report',
  standalone: true,
  imports: [InspectionTaskComponent, SharedModule],
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpSendReportComponent {
  payload$: Observable<InstallationInspectionOperatorRespondRequestTaskPayload> = this.inspectionService.payload$;
  requestTaskItem = toSignal(this.inspectionService.requestTaskItem$);
  isEditable = toSignal(this.inspectionService.isEditable$);
  isSubmitted: WritableSignal<boolean> = signal(false);

  vm: Signal<ViewModel> = computed(() => {
    const competentAuthority = this.requestTaskItem().requestInfo.competentAuthority;
    const requestId = this.requestTaskItem().requestInfo.id;

    return {
      competentAuthority,
      isEditable: this.isEditable(),
      isSubmitted: this.isSubmitted(),
      requestId,
    };
  });

  constructor(
    protected readonly inspectionService: InspectionService,
    readonly pendingRequest: PendingRequestService,
    protected readonly route: ActivatedRoute,
  ) {
    inspectionService.setType(this.route.snapshot.paramMap.get('type') as InspectionType);
  }

  onSubmit() {
    this.inspectionService
      .postInspectionForRespondTaskSaveOrSend({} as any, {}, true)
      .subscribe(() => this.isSubmitted.set(true));
  }
}
