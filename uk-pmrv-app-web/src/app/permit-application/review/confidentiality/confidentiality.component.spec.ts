import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { ConfidentialityComponent } from './confidentiality.component';

describe('ConfidentialityComponent', () => {
  let component: ConfidentialityComponent;
  let fixture: ComponentFixture<ConfidentialityComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'CONFIDENTIALITY_STATEMENT',
    },
  );

  @Component({
    selector: 'app-review-group-decision-container',
    template: `<div>
      Review group decision component.
      <div>Key:{{ groupKey }}</div>
      <div>Can edit:{{ canEdit }}</div>
    </div>`,
  })
  class MockDecisionComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  class Page extends BasePage<ConfidentialityComponent> {
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ConfidentialityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, SharedPermitModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
      declarations: [ConfidentialityComponent, MockDecisionComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockReviewState);
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate sections for review', () => {
    expect(
      page.reviewSections.map((section) => [
        section.querySelector('a').textContent.trim(),
        section.querySelector('govuk-tag').textContent.trim(),
      ]),
    ).toEqual([['Commercially confidential sections', 'not started']]);
  });
});
