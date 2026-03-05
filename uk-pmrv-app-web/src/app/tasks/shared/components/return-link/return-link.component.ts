import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { RequestTaskDTO } from 'pmrv-api';

@Component({
  selector: 'app-task-return-link',
  template: `
    <a govukLink [routerLink]="link">Return to: {{ linkText }}</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReturnLinkComponent implements OnInit {
  @Input() taskType: RequestTaskDTO['type'];
  @Input() levelsUp = 1;

  link: string;
  linkText: string;

  ngOnInit(): void {
    this.link = '../'.repeat(this.levelsUp).slice(0, -1);
    this.linkText = this.getTaskName(this.taskType);
  }

  private getTaskName(taskType: RequestTaskDTO['type']): string {
    switch (taskType) {
      case 'DOAL_APPLICATION_SUBMIT':
        return 'Determination of activity level change';
      case 'DOAL_AUTHORITY_RESPONSE':
        return 'Provide UK ETS Authority response for activity Level Change';
      case 'DOAL_APPLICATION_PEER_REVIEW':
        return 'Activity level determination peer review ';
      case 'DOAL_WAIT_FOR_PEER_REVIEW':
        return 'Activity level determination sent to peer reviewer';
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT':
        return 'Withholding of allowances';
      case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT':
        return 'Withdraw withholding of allowances notice ';
      default:
        return '';
    }
  }
}
