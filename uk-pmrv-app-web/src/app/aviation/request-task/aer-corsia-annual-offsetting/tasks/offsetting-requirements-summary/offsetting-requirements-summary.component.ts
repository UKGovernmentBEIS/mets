import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AnnualOffsettingRequirementsSummaryTemplateComponent } from '@aviation/shared/components/annual-offsetting-requirements-summary-template/annual-offsetting-requirements-summary-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaAnnualOffsetting } from 'pmrv-api';

import { aerCorsiaAnnualOffsettingQuery } from '../../aer-corsia-annual-offsetting.selectors';
import { sectionName } from '../../util/annual-offsetting.util';

interface ViewModel {
  heading: string;
  caption: string;
  annualOffsetting: AviationAerCorsiaAnnualOffsetting;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-annual-offsetting-requirements-summary',
  standalone: true,
  imports: [AnnualOffsettingRequirementsSummaryTemplateComponent, ReturnToLinkComponent, RouterLink, SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <app-page-heading [caption]="vm.caption">{{ vm.heading }}</app-page-heading>

      <app-annual-offsetting-requirements-summary-template
        [annualOffsetting]="vm.annualOffsetting"
        [isEditable]="vm.isEditable"></app-annual-offsetting-requirements-summary-template>

      <div class="govuk-button-group">
        <button (click)="onSubmit()" appPendingButton govukButton type="button" *ngIf="!vm.hideSubmit">
          Confirm and complete
        </button>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnnualOffsettingRequirementsSummaryComponent {
  private readonly annualOffsetting$ = this.store.pipe(aerCorsiaAnnualOffsettingQuery.selectAerCorsiaAnnualOffsetting);
  readonly isEditable = toSignal(this.store.pipe(requestTaskQuery.selectIsEditable));
  readonly annualOffsetting = toSignal(this.annualOffsetting$);
  readonly sectionsCompleted = toSignal(
    this.store.pipe(aerCorsiaAnnualOffsettingQuery.selectaAerCorsiaAnnualOffsettingSectionsCompleted),
  );

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const annualOffsetting = this.annualOffsetting();
    const sectionsCompleted = this.sectionsCompleted();

    return {
      heading: 'Check answers',
      caption: `${annualOffsetting.schemeYear} offsetting requirements`,
      annualOffsetting,
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
    this.annualOffsetting$
      .pipe(
        first(),
        switchMap((annualOffsetting) =>
          this.store.aerCorsiaAnnualOffsetting.saveOffsettingRequirement(annualOffsetting),
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
