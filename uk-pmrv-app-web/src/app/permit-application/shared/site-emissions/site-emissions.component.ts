import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';

import { map, Observable } from 'rxjs';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import { PermitMonitoringApproachSection } from 'pmrv-api';

import { MonitoringApproachDescriptionPipe } from '../../../shared/pipes/monitoring-approach-description.pipe';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { SiteEmissionItem } from './site-emission-item';

@Component({
  selector: 'app-site-emissions',
  templateUrl: './site-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SiteEmissionsComponent {
  displayRows: GovukSelectOption[] = [
    { text: 'Tonnes', value: true },
    { text: 'Percentage', value: false },
  ];
  form = this.formBuilder.group({ selectionControl: [true, { updateOn: 'change' }] });
  columns: GovukTableColumn[] = [
    { field: 'approach', header: 'Approach' },
    { field: 'marginal', header: 'Marginal' },
    { field: 'minimis', header: 'De-minimis' },
    { field: 'minor', header: 'Minor' },
    { field: 'major', header: 'Major' },
  ];

  tableRows$: Observable<SiteEmissionItem[]> = this.store
    .findTask('monitoringApproaches')
    .pipe(map((approaches) => this.tableData(approaches)));
  tableTotal$: Observable<SiteEmissionItem> = this.tableRows$.pipe(map((items) => items.slice(-1).pop()));

  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly formBuilder: UntypedFormBuilder,
  ) {}

  private tableData(approaches: { [key: string]: PermitMonitoringApproachSection }): SiteEmissionItem[] {
    const approachesData = Object.keys(approaches)
      .filter((type: PermitMonitoringApproachSection['type']) =>
        ['CALCULATION_CO2', 'FALLBACK', 'MEASUREMENT_CO2', 'MEASUREMENT_N2O', 'CALCULATION_PFC'].includes(type),
      )
      .map((type: PermitMonitoringApproachSection['type']) => this.createRow(approaches[type], type));

    const transferredApproachesData = Object.keys(approaches)
      .filter(
        (type: PermitMonitoringApproachSection['type']) =>
          ['CALCULATION_CO2', 'MEASUREMENT_CO2', 'MEASUREMENT_N2O'].includes(type) &&
          (approaches[type] as any)?.hasTransfer,
      )
      .map((type: PermitMonitoringApproachSection['type']) => {
        if (type === 'CALCULATION_CO2') {
          return this.createRow(approaches[type], type, 'Calculate Transferred CO2');
        } else if (type === 'MEASUREMENT_CO2') {
          return this.createRow(approaches[type], type, 'Measure Transferred CO2');
        } else if (type === 'MEASUREMENT_N2O') {
          return this.createRow(approaches[type], type, 'Measure Transferred N2O');
        }
      });

    return approachesData.length > 1 || transferredApproachesData.length > 0
      ? [
          ...approachesData,
          ...transferredApproachesData,
          this.createTotalRow([...approachesData, ...transferredApproachesData]),
        ]
      : approachesData;
  }

  private createTotalRow(siteEmissionItems: SiteEmissionItem[]): SiteEmissionItem {
    return {
      approach: 'Total',
      marginal: siteEmissionItems.reduce((total, item) => total + item.marginal, 0),
      minimis: siteEmissionItems.reduce((total, item) => total + item.minimis, 0),
      minor: siteEmissionItems.reduce((total, item) => total + item.minor, 0),
      major: siteEmissionItems.reduce((total, item) => total + item.major, 0),
    };
  }

  private createRow(
    approach: PermitMonitoringApproachSection,
    type: PermitMonitoringApproachSection['type'],
    tranferType?: any,
  ): SiteEmissionItem {
    let categories: any;
    const approachDescriptionPipe = new MonitoringApproachDescriptionPipe();

    if (type === 'MEASUREMENT_CO2' || type === 'MEASUREMENT_N2O') {
      categories = ((approach as any).emissionPointCategoryAppliedTiers as any[])?.map((tier) => {
        if (!tranferType) {
          if (tier?.emissionPointCategory?.transfer?.entryAccountingForTransfer) {
            return {
              ...tier.emissionPointCategory,
              annualEmittedCO2Tonnes: 0,
            };
          } else {
            return tier.emissionPointCategory;
          }
        } else {
          if (!tier?.emissionPointCategory?.transfer?.entryAccountingForTransfer) {
            return {
              ...tier.emissionPointCategory,
              annualEmittedCO2Tonnes: 0,
            };
          }
          if (
            tier?.emissionPointCategory?.transfer?.entryAccountingForTransfer &&
            tier?.emissionPointCategory?.transfer?.transferDirection !== 'RECEIVED_FROM_ANOTHER_INSTALLATION'
          ) {
            return {
              ...tier.emissionPointCategory,
              annualEmittedCO2Tonnes: -Math.abs(tier?.emissionPointCategory?.annualEmittedCO2Tonnes),
            };
          } else {
            return tier.emissionPointCategory;
          }
        }
      });
    } else
      categories = ((approach as any).sourceStreamCategoryAppliedTiers as any[])?.map((tier) => {
        if (!tranferType) {
          if (tier?.sourceStreamCategory?.transfer?.entryAccountingForTransfer) {
            return {
              ...tier.sourceStreamCategory,
              annualEmittedCO2Tonnes: 0,
            };
          } else {
            return tier.sourceStreamCategory;
          }
        } else {
          if (!tier?.sourceStreamCategory?.transfer?.entryAccountingForTransfer) {
            return {
              ...tier.sourceStreamCategory,
              annualEmittedCO2Tonnes: 0,
            };
          }
          if (
            tier?.sourceStreamCategory?.transfer?.entryAccountingForTransfer &&
            tier?.sourceStreamCategory?.transfer?.transferDirection !== 'RECEIVED_FROM_ANOTHER_INSTALLATION'
          ) {
            return {
              ...tier.sourceStreamCategory,
              annualEmittedCO2Tonnes: -Math.abs(tier?.sourceStreamCategory?.annualEmittedCO2Tonnes),
            };
          } else {
            return tier.sourceStreamCategory;
          }
        }
      });

    return {
      approach: tranferType
        ? tranferType
        : approachDescriptionPipe.transform(type as PermitMonitoringApproachSection['type']),
      marginal: this.approachTypeTotal(categories, 'MARGINAL'),
      minimis: this.approachTypeTotal(categories, 'DE_MINIMIS'),
      minor: this.approachTypeTotal(categories, 'MINOR'),
      major: this.approachTypeTotal(categories, 'MAJOR'),
    };
  }

  private approachTypeTotal(
    categories: {
      categoryType: 'DE_MINIMIS' | 'MAJOR' | 'MARGINAL' | 'MINOR';
      annualEmittedCO2Tonnes: number;
    }[],
    type: 'DE_MINIMIS' | 'MAJOR' | 'MARGINAL' | 'MINOR',
  ): number {
    return (categories ?? [])
      .filter((category) => category?.categoryType === type)
      .reduce((total, category) => total + (Number(category?.annualEmittedCO2Tonnes) || 0), 0);
  }
}
