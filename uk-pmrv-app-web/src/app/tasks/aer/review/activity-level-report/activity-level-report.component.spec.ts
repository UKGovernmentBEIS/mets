import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ActivityLevelReportComponent } from './activity-level-report.component';

describe('ActivityLevelReportComponent', () => {
  let component: ActivityLevelReportComponent;
  let fixture: ComponentFixture<ActivityLevelReportComponent>;
  let page: Page;
  let store: CommonTasksStore;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'ACTIVITY_LEVEL_REPORT',
    },
  );

  class Page extends BasePage<ActivityLevelReportComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get activityLevelGroup() {
      return this.query<HTMLDivElement>('app-activity-level-report-group');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, AerModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ActivityLevelReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(page.heading).toEqual('Activity level report');
    expect(page.activityLevelGroup.textContent).toBeTruthy();
  });
});
