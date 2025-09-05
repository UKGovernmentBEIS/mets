import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import {
  ALRActivityLevel,
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRRequestMetaData,
  HistoricalActivityLevel,
} from 'pmrv-api';

interface ViewModel {
  year: number;
  historicalActivityLevels: Array<HistoricalActivityLevel>;
  activityLevels: Array<ALRActivityLevel>;
  isEditable: boolean;
}

@Component({
  selector: 'app-alr-activity-levels',
  standalone: true,
  imports: [AlrTaskSharedModule, SharedModule, NgIf, RouterLink],
  templateUrl: './activity-levels.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrActivityLevelsComponent {
  private readonly requestMetadata = this.alrService.requestMetadata as Signal<ALRRequestMetaData>;
  private readonly isEditable = this.alrService.isEditable;
  private readonly payload = this.alrService.payload as Signal<ALRApplicationRegulatorReviewSubmitRequestTaskPayload>;

  vm: Signal<ViewModel> = computed(() => {
    const year = this.requestMetadata().year;
    const isEditable = this.isEditable();
    const { historicalActivityLevels = [], activityLevels = [] } = this.payload().regulatorReviewOutcome || {};

    return { year, historicalActivityLevels, activityLevels, isEditable };
  });

  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.router.navigate(['../estimates'], { relativeTo: this.route });
  }
}
