import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { NonComplianceModule } from '../../non-compliance.module';
import { mockState } from '../../testing/mock-non-compliance-submitted';
import { NonComplianceSummaryComponent } from './non-compliance-summary.component';

describe('NonComplianceSummaryComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: NonComplianceSummaryComponent;
  let fixture: ComponentFixture<NonComplianceSummaryComponent>;

  class Page extends BasePage<NonComplianceSummaryComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(NonComplianceSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Details of breach');
  });
});
