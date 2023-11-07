import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { VerifiedActivityLevelReportComponent } from './verified-activity-level-report.component';

describe('VerifiedActivityLevelReportComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: VerifiedActivityLevelReportComponent;
  let fixture: ComponentFixture<VerifiedActivityLevelReportComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'ACTIVITY_LEVEL_REPORT',
    },
  );

  class Page extends BasePage<VerifiedActivityLevelReportComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get activityLevelGroup() {
      return this.query<HTMLDivElement>('app-activity-level-report-group');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

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
    expect(page.activityLevelGroup.textContent).toBeTruthy();
  });
});
