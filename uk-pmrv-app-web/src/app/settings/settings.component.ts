import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { AuthStore } from '@core/store';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { LinkDirective } from 'govuk-components';

import { SettingsService } from 'pmrv-api';

interface ViewModel {
  isAviation: boolean;
  showEmissionFactors: boolean;
  showFees: boolean;
  showGlobalWarmingPotentials: boolean;
}

@Component({
  selector: 'app-settings',
  imports: [PageHeadingComponent, LinkDirective, RouterLink],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SettingsComponent {
  private readonly authStore = inject(AuthStore);
  private readonly settingsService = inject(SettingsService);

  private readonly accessibleSections = toSignal(
    this.settingsService.getAccessibleSections(this.authStore.currentDomain()),
    { initialValue: [] },
  );

  readonly vm: Signal<ViewModel> = computed(() => {
    const accessibleSections = this.accessibleSections();

    return {
      isAviation: this.authStore.currentDomain() === 'AVIATION',
      showEmissionFactors: accessibleSections.includes('EMISSION_FACTORS'),
      showFees: accessibleSections.includes('FEES'),
      showGlobalWarmingPotentials: accessibleSections.includes('GLOBAL_WARMING_POTENTIALS'),
    };
  });
}
