import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { MonitoringPlanVersionsComponent } from '@shared/components/monitoring-plan/monitoring-plan-versions.component';
import { SharedModule } from '@shared/shared.module';

import { MonitoringPlanVersion } from 'pmrv-api';

describe('MonitoringPlanVersionsComponent', () => {
  let component: MonitoringPlanVersionsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-monitoring-plan-versions [versions]="versions"></app-monitoring-plan-versions>
    `,
  })
  class TestComponent {
    versions: MonitoringPlanVersion[] = [
      {
        permitId: 'UK-E-IN-11399',
        endDate: '2021-03-10',
        permitConsolidationNumber: 15,
      },
      {
        permitId: 'UK-E-IN-11399',
        endDate: '2021-11-15',
        permitConsolidationNumber: 14,
      },
    ];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(MonitoringPlanVersionsComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the table', () => {
    expect(element.querySelector('caption').textContent.trim()).toEqual('Monitoring Plan Versions');
    expect(
      Array.from(element.querySelectorAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      ),
    ).toEqual([[], ['UK-E-IN-11399 v14', '15 Nov 2021'], ['UK-E-IN-11399 v15', '10 Mar 2021']]);
  });
});
