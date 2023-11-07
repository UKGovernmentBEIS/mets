import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationEmissionsGroupComponent } from '@shared/components/review-groups/calculation-emissions-group/calculation-emissions-group.component';
import { SharedModule } from '@shared/shared.module';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { Aer, CalculationOfCO2Emissions } from 'pmrv-api';

describe('CalculationEmissionsGroupComponent', () => {
  let component: CalculationEmissionsGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-calculation-emissions-group
        [data]="data"
        [statuses]="statuses"
        [isReview]="isReview"
      ></app-calculation-emissions-group>
    `,
  })
  class TestComponent {
    data = {
      naceCodes: undefined,
      abbreviations: undefined,
      additionalDocuments: undefined,
      confidentialityStatement: undefined,
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
        CALCULATION_CO2: {
          type: 'CALCULATION_CO2',
          sourceStreamEmissions: [
            {
              sourceStream: '324',
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['853'],
              biomassPercentages: {
                contains: false,
              },
              parameterMonitoringTiers: [
                {
                  tier: 'NO_TIER',
                  type: 'EMISSION_FACTOR',
                },
                {
                  tier: 'NO_TIER',
                  type: 'ACTIVITY_DATA',
                },
                {
                  tier: 'NO_TIER',
                  type: 'NET_CALORIFIC_VALUE',
                },
                {
                  tier: 'NO_TIER',
                  type: 'OXIDATION_FACTOR',
                },
              ] as any,
              parameterCalculationMethod: {
                type: 'MANUAL',
                emissionCalculationParamValues: {
                  emissionFactor: '3',
                  oxidationFactor: '3',
                  efMeasurementUnit: 'TONNES_OF_CO2_PER_NM3',
                  netCalorificValue: '4',
                  calculationCorrect: true,
                  ncvMeasurementUnit: 'GJ_PER_NM3',
                  totalReportableEmissions: '18',
                },
                calculationActivityDataCalculationMethod: {
                  type: 'CONTINUOUS_METERING',
                  totalMaterial: '2',
                  measurementUnit: 'NM3',
                },
              } as any,
              parameterMonitoringTierDiffReason: {
                type: 'DATA_GAP',
                reason: 'reason',
              },
            },
          ],
        } as CalculationOfCO2Emissions,
        MEASUREMENT_CO2: null,
      },
      emissionPoints: undefined,
      regulatedActivities: undefined,
      aerMonitoringPlanDeviation: undefined,
    } as Aer;
    statuses: TaskItemStatus[] = ['complete'];
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
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(CalculationEmissionsGroupComponent)).componentInstance;
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
      ['the reference Anthracite', 'emission source 1 reference', '18', '0', 'completed'],
      ['Total emissions', '', '18 tCO2e', '0 tCO2e', ''],
    ]);

    hostComponent.isReview = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLTableRowElement>('govuk-table tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([
      [],
      ['the reference Anthracite', 'emission source 1 reference', '18', '0', ''],
      ['Total emissions', '', '18 tCO2e', '0 tCO2e', ''],
    ]);
  });
});
