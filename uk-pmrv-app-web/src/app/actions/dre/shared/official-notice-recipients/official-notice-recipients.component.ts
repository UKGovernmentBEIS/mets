import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

import { DreApplicationSubmittedRequestActionPayload, RequestActionDTO } from 'pmrv-api';

@Component({
  selector: 'app-official-notice-recipients',
  templateUrl: './official-notice-recipients.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OfficialNoticeRecipientsComponent implements OnInit {
  @Input() action: RequestActionDTO;

  operators: string[];
  payload: DreApplicationSubmittedRequestActionPayload;

  ngOnInit(): void {
    this.payload = this.action.payload as DreApplicationSubmittedRequestActionPayload;
    this.operators = Object.keys(this.payload.usersInfo).filter(
      (userId) => userId !== this.payload.decisionNotification.signatory,
    );
  }
}
