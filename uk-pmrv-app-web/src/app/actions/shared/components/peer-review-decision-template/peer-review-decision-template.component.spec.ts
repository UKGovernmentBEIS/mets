import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionSharedModule } from '@actions/shared/action-shared-module';

import { PeerReviewDecision, RequestActionDTO } from 'pmrv-api';

describe('PeerReviewDecisionTemplateComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    standalone: false,
    template: `
      <app-peer-review-decision-template
        [requestActionType]="actionType"
        [decision]="decision"
        [submitter]="submitter"></app-peer-review-decision-template>
    `,
  })
  class TestComponent {
    actionType: RequestActionDTO['type'] = 'ALR_APPLICATION_PEER_REVIEW_ACCEPTED';
    decision: PeerReviewDecision = {
      type: 'AGREE',
      notes: 'My Notes',
    };
    submitter: 'Regulator1 England';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActionSharedModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
