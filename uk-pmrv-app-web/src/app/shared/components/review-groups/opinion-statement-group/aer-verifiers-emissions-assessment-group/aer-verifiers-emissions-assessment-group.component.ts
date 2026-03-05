import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { OpinionStatementEmissionsCalculationService } from '@shared/services/opinion-statement-emissions-calculation.service';

import { GovukTableColumn } from 'govuk-components';

import { MonitoringApproachTypeEmissions, OpinionStatement } from 'pmrv-api';

interface ReportableEmissionRow {
  label: string;
  emissions: string;
  biomass?: string;
  action?: string;
}

@Component({
  selector: 'app-aer-verifiers-emissions-assessment-group',
  templateUrl: './aer-verifiers-emissions-assessment-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [OpinionStatementEmissionsCalculationService],
})
export class AerVerifiersEmissionsAssessmentGroupComponent implements OnInit {
  @Input() opinionStatement: OpinionStatement;
  @Input() isEditable: boolean;

  private changeActionLink = '../review-emissions';

  reportableEmissionRows: ReportableEmissionRow[] = [];
  columns: GovukTableColumn<ReportableEmissionRow>[] = [
    { header: 'Approaches', field: 'label' },
    { header: 'Reportable emissions', field: 'emissions' },
    { header: 'Sustainable biomass', field: 'biomass' },
    { header: '', field: 'action' },
  ];

  constructor(
    private readonly opinionStatementEmissionsCalculationService: OpinionStatementEmissionsCalculationService,
  ) {}

  ngOnInit() {
    if (
      this.opinionStatement.operatorEmissionsAcceptable === false &&
      this.opinionStatement.monitoringApproachTypeEmissions
    ) {
      this.reportableEmissionRows = this.transformReportableEmissionsToRows(
        this.opinionStatement?.monitoringApproachTypeEmissions,
      );
    }
  }

  private transformReportableEmissionsToRows(
    monitoringApproachTypeEmissions?: MonitoringApproachTypeEmissions,
  ): ReportableEmissionRow[] {
    if (!monitoringApproachTypeEmissions) {
      return [];
    }
    const sortedReportableEmissions =
      this.opinionStatementEmissionsCalculationService.sortMonitoringApproachTypeEmissions(
        monitoringApproachTypeEmissions,
      );
    const reportableEmissionRows: ReportableEmissionRow[] = [];
    Object.entries(sortedReportableEmissions).forEach((item) => {
      reportableEmissionRows.push({
        label: this.opinionStatementEmissionsCalculationService.mapMonitoringApproachTypeCategoryToLabel(
          item[0] as keyof MonitoringApproachTypeEmissions,
        ),
        emissions: item[1].reportableEmissions,
        biomass: item[1]?.sustainableBiomass,
        action: this.changeActionLink,
      });
    });

    reportableEmissionRows.push({
      label: 'Total',
      emissions: this.opinionStatementEmissionsCalculationService.calculateTotalEmissions(
        monitoringApproachTypeEmissions,
      ),
      biomass: this.opinionStatementEmissionsCalculationService.calculateTotalBiomass(monitoringApproachTypeEmissions),
      action: null,
    });

    return reportableEmissionRows;
  }
}
