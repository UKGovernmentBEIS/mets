import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerRequestMetadata } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-verification-wait',
  templateUrl: './verification-wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationWaitComponent {
  requestTaskType$ = this.store.requestTaskType$;
  aerTitle$ = this.aerService.requestMetadata$.pipe(
    map((metadata) => (metadata as AerRequestMetadata).year + ' emissions report'),
  );
  readonly daysRemaining$ = this.store.pipe(map((state) => state.requestTaskItem.requestTask.daysRemaining));

  constructor(private readonly aerService: AerService, private readonly store: CommonTasksStore) {}
}
