import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

@Component({
  selector: 'app-mmp-installation-description-summary-template',
  templateUrl: './mmp-installation-description-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MmpInstallationDescriptionSummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() isPreview: boolean;
  @Input() hasBottomBorder = true;

  readonly files$ = this.store.pipe(
    map((state) => {
      return this.showOriginal
        ? ((state as any).originalPermitContainer.permit.monitoringMethodologyPlans?.digitizedPlan
            ?.installationDescription?.flowDiagrams ?? [])
        : (state.permit.monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.flowDiagrams ?? []);
    }),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
