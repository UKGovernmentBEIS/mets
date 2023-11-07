import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { AirImprovement } from 'pmrv-api';

import { AirActionSubmittedModule } from '../air-action-submitted.module';
import { mockAirApplicationSubmittedRequestActionPayload, mockState } from '../testing/mock-air-submitted';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const reference = '1';
  const currentItem: AirImprovement = mockAirApplicationSubmittedRequestActionPayload.airImprovements[reference];
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

    get airOperatorImprovementItem() {
      return this.query('app-air-improvement-item');
    }

    get airOperatorResponseItem() {
      return this.query('app-air-operator-response-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirActionSubmittedModule, RouterTestingModule],
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
    expect(page.heading1.textContent.trim()).toEqual('Item 1: F1: Acetylene: major: emission factor');
    expect(page.airOperatorImprovementItem).toBeTruthy();
    expect(page.airOperatorResponseItem).toBeTruthy();
  });
});
