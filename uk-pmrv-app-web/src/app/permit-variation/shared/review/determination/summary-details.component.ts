import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { SummaryDetailsAbstractComponent } from '../../../../permit-application/review/determination/summary/summary-details-abstract.component';
import { reasonTemplatesMap } from '../../../../permit-variation/review/determination/reason-template/reason-template.type';
import { getVariationScheduleItems } from '../../../../permit-variation/review/review';
import { PermitVariationStore } from '../../../store/permit-variation.store';

@Component({
  selector: 'app-permit-variation-determination-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SummaryDetailsComponent extends SummaryDetailsAbstractComponent {
  @Input() cssClass: string;
  @Input() changePerStage: boolean;

  isVariationRegulatorLed = this.store.isVariationRegulatorLedRequest;
  isNotVariationRegulatorLed = !this.isVariationRegulatorLed;

  reasonTemplate$ = this.store.pipe(
    map((state) =>
      state.determination.reasonTemplate === 'OTHER'
        ? state.determination.reasonTemplateOtherSummary
        : reasonTemplatesMap[state.determination.reasonTemplate],
    ),
  );

  variationScheduleItems$: Observable<string[]> = this.store.pipe(map((state) => getVariationScheduleItems(state)));

  constructor(
    protected readonly store: PermitVariationStore,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }
}
