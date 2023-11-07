import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateReviewed } from '../testing/mock-aer-submitted';
import { VerifiedActivityLevelReportComponent } from './verified-activity-level-report.component';

describe('VerifiedActivityLevelReportComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: VerifiedActivityLevelReportComponent;
  let fixture: ComponentFixture<VerifiedActivityLevelReportComponent>;

  class Page extends BasePage<VerifiedActivityLevelReportComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get activityLevelReportGroup() {
      return this.query<HTMLDivElement>('app-activity-level-report-group');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateReviewed);

    fixture = TestBed.createComponent(VerifiedActivityLevelReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Verification report of the activity level report');
    expect(page.activityLevelReportGroup.textContent).toBeTruthy();
  });
});
