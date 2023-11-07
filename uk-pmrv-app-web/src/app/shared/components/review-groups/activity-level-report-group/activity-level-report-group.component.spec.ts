import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityLevelReportGroupComponent } from './activity-level-report-group.component';

describe('ActivityLevelReportGroupComponent', () => {
  let component: ActivityLevelReportGroupComponent;
  let fixture: ComponentFixture<ActivityLevelReportGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityLevelReportGroupComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityLevelReportGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
