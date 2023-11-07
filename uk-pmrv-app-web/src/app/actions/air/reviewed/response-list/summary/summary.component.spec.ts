import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement } from 'pmrv-api';

import { mockState } from '../../../submitted/testing/mock-air-submitted';
import { AirActionReviewedModule } from '../../air-action-reviewed.module';
import { mockAirApplicationReviewedRequestActionPayload } from '../../testing/mock-air-reviewed';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const reference = '1';
  const currentItem: AirImprovement = mockAirApplicationReviewedRequestActionPayload.airImprovements[reference];
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub(
    { taskId: mockState.action.id, id: reference },
    null,
    {
      airImprovement: currentItem,
    },
  );

  class Page extends BasePage<SummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get airImprovementItem() {
      return this.query('app-air-improvement-item');
    }

    get airOperatorResponseItem() {
      return this.query('app-air-operator-response-item');
    }

    get airRegulatorResponseItem() {
      return this.query('app-air-regulator-response-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirActionReviewedModule, RouterTestingModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual('Review information about this improvement');
    expect(page.airImprovementItem).toBeTruthy();
    expect(page.airOperatorResponseItem).toBeTruthy();
    expect(page.airRegulatorResponseItem).toBeTruthy();
  });
});
