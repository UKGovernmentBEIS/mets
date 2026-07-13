import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

import { ActivityLevel, ALRActivityLevel, HistoricalActivityLevel } from 'pmrv-api';

import {
  alrChangeTypeLabelsMap,
  alrChangeTypeLabelsMap2027,
  doalChangeTypeLabelsMap,
  newSubInstallationNameLabelsMap,
} from '../doal/activity-level-label.map';

type ActivityLevelType = ActivityLevel & HistoricalActivityLevel & ALRActivityLevel;

@Component({
  selector: 'app-activity-level-list-template',
  standalone: false,
  templateUrl: './activity-level-list-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelListTemplateComponent implements OnInit {
  @Input() data: ActivityLevelType[];
  @Input() heading: string;
  @Input() historical: boolean;
  @Input() editable: boolean;
  @Input() isDoal: boolean;
  @Input() year: number;

  columns: GovukTableColumn[] = [];

  dataSorted: ActivityLevelType[];

  subInstallationNameLabelsMap = newSubInstallationNameLabelsMap;
  changeTypeLabelsMap;

  ngOnInit(): void {
    this.changeTypeLabelsMap = this.isDoal
      ? doalChangeTypeLabelsMap
      : this.year >= 2027
        ? { ...alrChangeTypeLabelsMap2027, NEW_SUB_INSTALLATION: 'New sub-installation' }
        : { ...alrChangeTypeLabelsMap, NEW_SUB_INSTALLATION: 'New sub-installation' };

    const commonColumns = [
      { field: 'year', header: 'Year', widthClass: 'app-column-width-5-per' },
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
      this.columns = this.columns.concat(
        { field: 'change', header: '', widthClass: 'app-column-width-5-per' },
        { field: 'delete', header: '', widthClass: 'app-column-width-5-per' },
      );
    }
  }
}
