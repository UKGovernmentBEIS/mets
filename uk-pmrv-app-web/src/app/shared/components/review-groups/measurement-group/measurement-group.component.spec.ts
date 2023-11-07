import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { Aer, MeasurementOfCO2Emissions } from 'pmrv-api';

import { SharedModule } from '../../../shared.module';
import { TaskItemStatus } from '../../../task-list/task-list.interface';
import { MeasurementGroupComponent } from './measurement-group.component';

describe('MeasurementGroupComponent', () => {
  let component: MeasurementGroupComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;
  @Component({
    template: `
      <app-measurement-group
        [data]="data"
        [statuses]="statuses"
        [taskKey]="taskKey"
        [isReview]="isReview"
      ></app-measurement-group>
    `,
  })
  class TestComponent {
    data = {
      naceCodes: undefined,
      abbreviations: undefined,
      additionalDocuments: undefined,
      confidentialityStatement: undefined,
      emissionPoints: [
        {
          id: '167',
          reference: 'Quis perferendis fug',
          description: 'Et nisi quos illo ir',
        },
      ],
      sourceStreams: [
        {
          id: '324',
          description: 'ANTHRACITE',
          type: 'AMMONIA_FUEL_AS_PROCESS_INPUT',
          reference: 'the reference',
        },
        {
          id: '325',
          description: 'BIODIESELS',
          type: 'CEMENT_CLINKER_CKD',
          reference: 'the other reference',
        },
      ],
      emissionSources: [
        {
          id: '853',
          description: 'emission source 1 description',
          reference: 'emission source 1 reference',
        },
        {
          id: '854',
          description: 'emission source 2 description',
          reference: 'emission source 2 reference',
        },
      ],
      pollutantRegisterActivities: undefined,
      monitoringApproachEmissions: {
        MEASUREMENT_CO2: {
          type: 'MEASUREMENT_CO2',
          hasTransfer: true,
          emissionPointEmissions: [
            {
              id: 'f61d8b13-74c4-4da8-b59c-368f82d955fb',
              tier: 'TIER_3',
              emissionPoint: '167',
              sourceStreams: ['324'],
              emissionSources: ['854'],
            },
          ],
        } as MeasurementOfCO2Emissions,
      },

      regulatedActivities: undefined,
      aerMonitoringPlanDeviation: undefined,
    } as Aer;
    statuses: TaskItemStatus[] = ['complete'];
    taskKey = 'MEASUREMENT_CO2';
    isReview = false;
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
    component = fixture.debugElement.query(By.directive(MeasurementGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the table', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLTableRowElement>('govuk-table tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([
      [],
      ['Quis perferendis fug', 'the reference Anthracite', 'emission source 2 reference', '0', '0', 'completed'],
      ['Total emissions', '', '', '0 tCO2e', '0 tCO2e', ''],
    ]);
  });
});
