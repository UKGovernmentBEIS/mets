import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockReviewState } from '../../testing/mock-state';
import { MeasurementComponent } from './measurement.component';

describe('MeasurementComponent', () => {
  let component: MeasurementComponent;
  let fixture: ComponentFixture<MeasurementComponent>;
  let page: Page;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MEASUREMENT_CO2',
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

  class Page extends BasePage<MeasurementComponent> {
    get tierRows(): HTMLTableRowElement[] {
      return Array.from(this.queryAll<HTMLTableRowElement>('table tbody tr'));
    }

    get staticSections(): HTMLAnchorElement[] {
      return Array.from(this.queryAll<HTMLAnchorElement>('ul > li > span.app-task-list__task-name > a'));
    }
  }

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
      declarations: [MeasurementComponent, MockDecisionComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState({
      ...mockReviewState,
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: mockPermitCompletePayload.permitSectionsCompleted,
      reviewGroupDecisions: {
        MEASUREMENT_CO2: {
          type: 'ACCEPTED',
          details: { notes: 'notes' },
        },
      },
      reviewSectionsCompleted: {
        MEASUREMENT_CO2: true,
      },
    });
    fixture = TestBed.createComponent(MeasurementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render emission point names', () => {
    expect(page.tierRows.map((row) => row.cells[0].textContent.trim())).toEqual(
      expect.arrayContaining(['EP1 Emission Point 1: Major']),
    );
  });

  it('should render emissions info', () => {
    expect(page.tierRows.map((row) => row.cells[1].textContent.trim())).toEqual(expect.arrayContaining(['23.8 t']));
  });

  it('should render measured emissions info', () => {
    expect(page.tierRows.map((row) => row.cells[2].textContent.trim())).toEqual(expect.arrayContaining(['Tier 1']));
  });

  it('should render the static sections', () => {
    expect(page.staticSections.map((el) => el.textContent.trim())).toEqual(
      expect.arrayContaining([
        'Approach description',
        'Determination of emissions by measurement',
        'Determination of reference period',
        'Calculation of gas flow',
        'Biomass emissions',
        'Corroborating calculations',
      ]),
    );
  });
});
