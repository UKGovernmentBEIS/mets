import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-comments-response-container',
  templateUrl: './comments-response-container.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentsResponseContainerComponent {
  title$ = this.airService.title$;
  isEditable$ = this.airService.isEditable$;
  airPayload$ = this.airService.payload$ as Observable<AirApplicationRespondToRegulatorCommentsRequestTaskPayload>;
  regulatorImprovementResponses$ = (
    this.airService.payload$ as Observable<AirApplicationRespondToRegulatorCommentsRequestTaskPayload>
  ).pipe(map((payload) => this.filterOutRespondedItems(payload)));
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  readonly daysRemaining$ = this.airService.daysRemaining$;

  constructor(
    private readonly airService: AirService,
    private readonly router: Router,
  ) {}

  /**
   * Filters out regulatorImprovementResponses where there is an index found in respondedItems.
   *  - Map respondedItems as array of strings.
   *  - Filter indexes from regulatorImprovementResponses as array, where not found in respondedItems
   *  - Rebuild array into object
   *
   * @param payload
   * @private
   */
  private filterOutRespondedItems(
    payload: AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  ): AirApplicationRespondToRegulatorCommentsRequestTaskPayload['regulatorImprovementResponses'] {
    const respondedItemsStringArray = (payload?.respondedItems ?? []).map((item) => item.toString());
    const regulatorImprovementResponsesArray = Object.entries(payload?.regulatorImprovementResponses ?? []).filter(
      ([key]) => !respondedItemsStringArray.includes(key),
    );
    return Object.fromEntries(regulatorImprovementResponsesArray);
  }
}
