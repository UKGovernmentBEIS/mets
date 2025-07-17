import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { ActivityLevel, HistoricalActivityLevel } from 'pmrv-api';

import { GovukTableColumn } from '../../../../../../projects/govuk-components/src/public-api';
import { changeTypeLabelsMap, subInstallationNameLabelsMap } from '../activity-level-label.map';

type ActivityLevelType = ActivityLevel & HistoricalActivityLevel;

@Component({
  selector: 'app-doal-activity-level-list-template',
  templateUrl: './activity-level-list-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelListTemplateComponent implements OnInit {
  @Input() data: ActivityLevelType[];
  @Input() heading: string;
  @Input() historical: boolean;
  @Input() editable: boolean;

  columns: GovukTableColumn[] = [];

  dataSorted: ActivityLevelType[];

  subInstallationNameLabelsMap = subInstallationNameLabelsMap;
  changeTypeLabelsMap = changeTypeLabelsMap;

  ngOnInit(): void {
    const commonColumns = [
      { field: 'year', header: 'Year' },
      { field: 'subInstallationName', header: 'Sub-installation' },
      { field: 'changeType', header: 'Change type' },
      { field: 'changedActivityLevel', header: 'Amount' },
    ];

    let hasComments = false;

    this.dataSorted = this.data.sort((a, b) =>
      a.year - b.year === 0 ? a.subInstallationName.localeCompare(b.subInstallationName) : a.year - b.year,
    );

    this.dataSorted.forEach((el) => {
      if (el.comments) {
        hasComments = true;
      }
    });

    this.columns = hasComments ? [...commonColumns, { field: 'comments', header: 'Comments' }] : commonColumns;

    if (this.historical) {
      this.columns = this.columns.concat({ field: 'creationDate', header: 'Last updated' });
    }

    if (!this.historical && this.editable) {
      this.columns = this.columns.concat({ field: 'change', header: '' }, { field: 'delete', header: '' });
    }
  }
}
