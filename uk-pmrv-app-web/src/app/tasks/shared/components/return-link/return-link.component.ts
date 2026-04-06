import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { RequestTaskDTO } from 'pmrv-api';

import { getTaskName } from './return-link.utils';

@Component({
  selector: 'app-task-return-link',
  standalone: false,
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
    this.linkText = getTaskName(this.taskType);
  }
}
