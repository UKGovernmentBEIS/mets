import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { AviationAerCorsia3YearPeriodOffsettingTableData } from '@aviation/shared/types';
import { getTableData } from '@aviation/shared/utils/3year-period-offsetting-shared.util';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsia3YearPeriodOffsetting } from 'pmrv-api';

import { ThreeYearOffsettingRequirementsTableTemplateComponent } from '../../../../shared/components/offsetting-requirements-table-template/offsetting-requirements-table-template.component';
import { aerCorsia3YearOffsettingQuery } from '../../aer-corsia-3year-period-offsetting.selectors';
import { sectionName } from '../../util/3year-period-offsetting.util';

interface ViewModel {
  heading: string;
  caption: string;
  data: AviationAerCorsia3YearPeriodOffsettingTableData[];
  operatorHaveOffsettingRequirements: AviationAerCorsia3YearPeriodOffsetting['operatorHaveOffsettingRequirements'];
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-3year-offsetting-requirements-summary',
  standalone: true,
  imports: [ReturnToLinkComponent, RouterLink, SharedModule, ThreeYearOffsettingRequirementsTableTemplateComponent],
  templateUrl: './offsetting-requirements-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThreeYearOffsettingRequirementsSummaryComponent {
  private readonly offsettingRequirements$ = this.store.pipe(
    aerCorsia3YearOffsettingQuery.selectAerCorsia3YearOffsetting,
  );
  private readonly isEditable = toSignal(this.store.pipe(requestTaskQuery.selectIsEditable));
  private readonly offsettingRequirements = toSignal(this.offsettingRequirements$);
  private readonly sectionsCompleted = toSignal(
    this.store.pipe(aerCorsia3YearOffsettingQuery.selectAerCorsia3YearPeriodOffsettingSectionsCompleted),
  );
  private readonly getTableData: Signal<AviationAerCorsia3YearPeriodOffsettingTableData[]> = computed(() => {
    return getTableData(this.offsettingRequirements());
  });

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const offsettingRequirements = this.offsettingRequirements();
    const schemeYears = offsettingRequirements?.schemeYears || [];
    const sectionsCompleted = this.sectionsCompleted();
    const data = this.getTableData();

    return {
      heading: 'Check answers',
      caption: `${schemeYears[0]} - ${schemeYears[schemeYears.length - 1]} offsetting requirements`,
      data,
      operatorHaveOffsettingRequirements: offsettingRequirements.operatorHaveOffsettingRequirements,
      isEditable,
      hideSubmit: !isEditable || sectionsCompleted[sectionName],
    };
  });

  constructor(
    private readonly store: RequestTaskStore,
    private readonly pendingRequestService: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.offsettingRequirements$
      .pipe(
        first(),
        switchMap((annualOffsetting) =>
          this.store.aerCorsia3YearPeriodOffsetting.saveOffsettingRequirement(annualOffsetting),
        ),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../..'], {
          relativeTo: this.route,
        });
      });
  }
}
