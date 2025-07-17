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
import { ManagementProceduresComponent } from './management-procedures.component';

describe('ManagementProceduresComponent', () => {
  let component: ManagementProceduresComponent;
  let fixture: ComponentFixture<ManagementProceduresComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MANAGEMENT_PROCEDURES',
    },
  );

  @Component({
    selector: 'app-review-group-decision-container',
    template: `
      <div>
        Review group decision component.
        <div>Key:{{ groupKey }}</div>
        <div>Can edit:{{ canEdit }}</div>
      </div>
    `,
  })
  class MockDecisionComponent {
    @Input() groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'];
    @Input() canEdit = true;
    @Output() readonly notification = new EventEmitter<boolean>();
  }

  class Page extends BasePage<ManagementProceduresComponent> {
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ManagementProceduresComponent);
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
      declarations: [ManagementProceduresComponent, MockDecisionComponent],
    }).compileComponents();
  });

  describe('without review group decision', () => {
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
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Monitoring and reporting roles', 'not started'],
        ['Assignment of responsibilities', 'completed'],
        ['Monitoring plan appropriateness', 'not started'],
        ['Data flow activities', 'not started'],
        ['Quality assurance of IT used for data flow activities', 'not started'],
        ['Review and validation of data', 'not started'],
        ['Assessing and controlling risks', 'not started'],
        ['Quality assurance of metering and measuring equipment', 'not started'],
        ['Corrections and corrective actions', 'not started'],
        ['Control of outsourced activities', 'not started'],
        ['Record keeping and documentation', 'not started'],
        ['Environmental management system', 'not started'],
      ]);
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          MANAGEMENT_PROCEDURES: {
            type: 'ACCEPTED',
            details: { notes: 'notes' },
          },
        },
        reviewSectionsCompleted: {
          MANAGEMENT_PROCEDURES: true,
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display appropriate sections for review', () => {
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Monitoring and reporting roles', 'not started'],
        ['Assignment of responsibilities', 'completed'],
        ['Monitoring plan appropriateness', 'not started'],
        ['Data flow activities', 'not started'],
        ['Quality assurance of IT used for data flow activities', 'not started'],
        ['Review and validation of data', 'not started'],
        ['Assessing and controlling risks', 'not started'],
        ['Quality assurance of metering and measuring equipment', 'not started'],
        ['Corrections and corrective actions', 'not started'],
        ['Control of outsourced activities', 'not started'],
        ['Record keeping and documentation', 'not started'],
        ['Environmental management system', 'not started'],
      ]);
    });
  });
});
