import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationOfPfcEmissions } from 'pmrv-api';
import { Aer } from 'pmrv-api';

import { SharedModule } from '../../../shared.module';
import { TaskItemStatus } from '../../../task-list/task-list.interface';
import { PfcGroupComponent } from './pfc-group.component';

describe('PfcGroupComponent', () => {
  let component: PfcGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: ` <app-pfc-group [data]="data" [statuses]="statuses" [isReview]="isReview"></app-pfc-group> `,
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
        CALCULATION_PFC: {
          type: 'CALCULATION_PFC',
          sourceStreamEmissions: [
            {
              id: '816528ef-c303-4a95-9d21-9199264672e8',
              amountOfCF4: '0.204',
              amountOfC2F6: '27.744',
              sourceStream: '324',
              durationRange: {
                fullYearCovered: true,
              },
              emissionSources: ['853'],
              totalCF4Emissions: '1507.56',
              calculationCorrect: true,
              totalC2F6Emissions: '338476.8',
              reportableEmissions: '11332812.0',
              totalPrimaryAluminium: '34',
              massBalanceApproachUsed: true,
              parameterMonitoringTier: {
                activityDataTier: 'TIER_1',
                emissionFactorTier: 'TIER_1',
              },
              parameterMonitoringTierDiffReason: {
                type: 'DATA_GAP',
                reason: 'erte',
              },
              pfcSourceStreamEmissionCalculationMethodData: {
                calculationMethod: 'SLOPE',
                c2F6WeightFraction: 4,
                anodeEffectsPerCellDay: 1,
                slopeCF4EmissionFactor: 3,
                percentageOfCollectionEfficiency: 3,
                averageDurationOfAnodeEffectsInMinutes: 2,
              } as any,
            },
          ],
        } as CalculationOfPfcEmissions,
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
    component = fixture.debugElement.query(By.directive(PfcGroupComponent)).componentInstance;
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
      ['the reference Anthracite', 'emission source 1 reference', 'Slope', '11332812', 'completed'],
      ['Total emissions', '', '', '11332812 tCO2e', ''],
    ]);

    hostComponent.isReview = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLTableRowElement>('govuk-table tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([
      [],
      ['the reference Anthracite', 'emission source 1 reference', 'Slope', '11332812', ''],
      ['Total emissions', '', '', '11332812 tCO2e', ''],
    ]);
  });
});
