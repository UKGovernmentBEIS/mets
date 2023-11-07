import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { AirActionReviewedModule } from '../../air-action-reviewed.module';
import { mockState } from '../../testing/mock-air-reviewed';
import { ProvideSummaryComponent } from './provide-summary.component';

describe('ReportSummaryComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ProvideSummaryComponent;
  let fixture: ComponentFixture<ProvideSummaryComponent>;

  class Page extends BasePage<ProvideSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get airRegulatorProvideSummary() {
      return this.query('app-air-regulator-provide-summary');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AirActionReviewedModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ProvideSummaryComponent);
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
    expect(page.heading1.textContent.trim()).toEqual('Review summary');
    expect(page.airRegulatorProvideSummary).toBeTruthy();
  });
});
