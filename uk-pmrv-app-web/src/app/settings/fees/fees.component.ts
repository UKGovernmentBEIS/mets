import { CurrencyPipe, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { GovukTableColumn, LinkDirective, NotificationBannerComponent, TableComponent } from 'govuk-components';

import { SettingsFeesPermissionService } from '../core/settings-fees-permission.service';
import { FeeRow } from './fees.model';
import { FeesService } from './fees.service';

interface ViewModel {
  fees: FeeRow[];
  canExecute: boolean;
  columns: GovukTableColumn<FeeRow>[];
}

@Component({
  selector: 'app-fees',
  imports: [
    PageHeadingComponent,
    TableComponent,
    LinkDirective,
    RouterLink,
    NotificationBannerComponent,
    CurrencyPipe,
    DatePipe,
  ],
  templateUrl: './fees.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeesComponent {
  private readonly feesService = inject(FeesService);
  private readonly router = inject(Router);
  private readonly settingsFeesPermissionService = inject(SettingsFeesPermissionService);

  private readonly fees = toSignal(this.feesService.getFees(), { initialValue: [] as FeeRow[] });
  private readonly canExecute = toSignal(this.settingsFeesPermissionService.canExecute(), { initialValue: false });

  readonly notification = this.router.currentNavigation()?.extras.state?.notification;

  readonly vm: Signal<ViewModel> = computed(() => {
    const canExecute = this.canExecute();

    return {
      fees: this.fees(),
      canExecute,
      columns: [
        { field: 'workflow', header: 'Workflow' },
        { field: 'currentAmount', header: 'Current payment amount' },
        { field: 'scheduledChange', header: 'Scheduled change' },
        ...(canExecute ? [{ field: 'key', header: 'Actions', hiddenHeader: true }] : []),
      ] as GovukTableColumn<FeeRow>[],
    };
  });
}
