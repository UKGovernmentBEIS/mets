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
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockReviewState } from '../../testing/mock-state';
import { InherentCO2Component } from './inherent-co2.component';

describe('InherentCO2Component', () => {
  let component: InherentCO2Component;
  let fixture: ComponentFixture<InherentCO2Component>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'INHERENT_CO2',
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

  class Page extends BasePage<InherentCO2Component> {
    get reviewSections() {
      return this.queryAll<HTMLLIElement>('li[app-task-item]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(InherentCO2Component);
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
      declarations: [InherentCO2Component, MockDecisionComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockReviewState,
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: mockPermitCompletePayload.permitSectionsCompleted,
      reviewGroupDecisions: {
        INHERENT_CO2: {
          type: 'REJECTED',
          details: { notes: 'notes' },
        },
      },
      reviewSectionsCompleted: {
        INHERENT_CO2: true,
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
    ).toEqual([['Inherent CO2 emissions', 'completed']]);
  });
});
