import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { mockState } from '../../testing/mock-aer-reviewed';
import { VirActionReviewedModule } from '../../vir-action-reviewed.module';
import { ReportSummaryComponent } from './report-summary.component';

describe('ReportSummaryComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ReportSummaryComponent;
  let fixture: ComponentFixture<ReportSummaryComponent>;

  class Page extends BasePage<ReportSummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get regulatorCreateSummary() {
      return this.query('app-regulator-create-summary');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirActionReviewedModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ReportSummaryComponent);
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
    expect(page.heading1.textContent.trim()).toEqual('Create summary');
    expect(page.regulatorCreateSummary).toBeTruthy();
  });
});
