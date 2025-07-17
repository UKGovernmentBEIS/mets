import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import {
  CessationNotification,
  NonSignificantChange,
  OtherFactor,
  PermitNotificationApplicationSubmitRequestTaskPayload,
  TemporaryChange,
  TemporaryFactor,
  TemporarySuspension,
} from 'pmrv-api';

import { RequestTaskFileService } from '../../../../shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { endDateConditionalValidator, endDateValidator } from '../../core/permit-notification.validator';
import { PERMIT_NOTIFICATION_TASK_FORM } from '../../core/permit-notification-task-form.token';
import { NonSignificantChangeComponent } from '../../shared/components/non-significant-change/non-significant-change.component';
import { OtherFactorComponent } from '../../shared/components/other-factor/other-factor.component';
import { PermanentCessationComponent } from '../../shared/components/permanent-cessation/permanent-cessation.component';
import { TemporaryChangeComponent } from '../../shared/components/temporary-change/temporary-change.component';
import { TemporaryFactorComponent } from '../../shared/components/temporary-factor/temporary-factor.component';
import { TemporarySuspensionComponent } from '../../shared/components/temporary-suspension/temporary-suspension.component';

export const descriptionFormProvider = {
  provide: PERMIT_NOTIFICATION_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    requestTaskFileService: RequestTaskFileService,
  ): UntypedFormGroup => {
    const state = store.getValue();

    const payload = state.requestTaskItem.requestTask.payload as PermitNotificationApplicationSubmitRequestTaskPayload;
    const disabled = !state.isEditable;

    return fb.group({
      notification: fb.group(
        {
          ...createFormByType(payload.permitNotification, disabled),
          documents: requestTaskFileService.buildFormControl(
            store.requestTaskId,
            payload.permitNotification?.documents ?? [],
            payload?.permitNotificationAttachments,
            'PERMIT_NOTIFICATION_UPLOAD_ATTACHMENT',
            false,
            !state.isEditable,
          ),
        },
        {
          validators:
            payload.permitNotification.type === 'NON_SIGNIFICANT_CHANGE'
              ? []
              : payload.permitNotification.type === 'OTHER_FACTOR'
                ? [endDateConditionalValidator()]
                : payload.permitNotification.type === 'CESSATION'
                  ? []
                  : [endDateValidator()],
        },
      ),
    });
  },
};

function createFormByType(
  value:
    | TemporaryFactor
    | TemporaryChange
    | TemporarySuspension
    | CessationNotification
    | NonSignificantChange
    | OtherFactor,
  disabled: boolean,
): any {
  switch (value.type) {
    case 'TEMPORARY_FACTOR':
      return TemporaryFactorComponent.controlsFactory(value as TemporaryFactor, disabled);
    case 'TEMPORARY_CHANGE':
      return TemporaryChangeComponent.controlsFactory(value as TemporaryChange, disabled);
    case 'TEMPORARY_SUSPENSION':
      return TemporarySuspensionComponent.controlsFactory(value as TemporarySuspension, disabled);
    case 'CESSATION':
      return PermanentCessationComponent.controlsFactory(value as CessationNotification, disabled);
    case 'NON_SIGNIFICANT_CHANGE':
      return NonSignificantChangeComponent.controlsFactory(value as NonSignificantChange, disabled);
    case 'OTHER_FACTOR':
      return OtherFactorComponent.controlsFactory(value as OtherFactor, disabled);
  }
}
