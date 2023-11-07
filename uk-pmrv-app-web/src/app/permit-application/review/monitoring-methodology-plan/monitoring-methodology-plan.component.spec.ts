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
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan.component';

describe('MonitoringMethodologyPlanComponent', () => {
  let component: MonitoringMethodologyPlanComponent;
  let fixture: ComponentFixture<MonitoringMethodologyPlanComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MONITORING_METHODOLOGY_PLAN',
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

  class Page extends BasePage<MonitoringMethodologyPlanComponent> {
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
    get file() {
      const roles = this.queryAll<HTMLDListElement>('dd');
      return roles.slice(roles.length - 1)[0];
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MonitoringMethodologyPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
      declarations: [MonitoringMethodologyPlanComponent, MockDecisionComponent],
    }).compileComponents();
  });

  describe('without review group decision', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({ ...mockReviewState });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should mention the attachment', () => {
      expect(page.file.textContent.trim()).toEqual('No');
      expect(
        page.reviewSections.map((section) => [
          section.querySelector('a')?.textContent.trim() ?? section.querySelector('strong').textContent.trim(),
          section.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([['Monitoring methodology plan', 'not started']]);
    });
  });

  describe('with review group decision summary', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockReviewState,
        reviewGroupDecisions: {
          MONITORING_METHODOLOGY_PLAN: {
            type: 'ACCEPTED',
            details: { notes: 'notes' },
          },
        },
        reviewSectionsCompleted: {
          MONITORING_METHODOLOGY_PLAN: true,
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
      ).toEqual([['Monitoring methodology plan', 'not started']]);
    });
  });
});
