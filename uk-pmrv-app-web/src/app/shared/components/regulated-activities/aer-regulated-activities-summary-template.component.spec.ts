import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { AerRegulatedActivitiesSummaryTemplateComponent } from '@shared/components/regulated-activities/aer-regulated-activities-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { AerRegulatedActivity } from 'pmrv-api';

describe('RegulatedActivitiesSummaryTemplateComponent', () => {
  let component: AerRegulatedActivitiesSummaryTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    standalone: false,
    template: `
      <app-aer-regulated-activities-summary-template
        [activity]="activity"
        [isEditable]="isEditable"></app-aer-regulated-activities-summary-template>
    `,
  })
  class TestComponent {
    activity: AerRegulatedActivity = {
      id: '324',
      type: 'AMMONIA_PRODUCTION',
      capacity: '100',
      capacityUnit: 'KVA',
      hasEnergyCrf: true,
      hasIndustrialCrf: true,
      hasWasteCrf: true,
      energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
      industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
      wasteCrf: '_5_A_1_A_SOLID_WASTE_DISPOSAL_TO_LAND',
    };
    isEditable = true;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(
      By.directive(AerRegulatedActivitiesSummaryTemplateComponent),
    ).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary', () => {
    expect(Array.from(element.querySelectorAll('dd')).map((el) => el.textContent.trim())).toEqual([
      'Ammonia production (Carbon dioxide)',
      'Change',
      '100 kVA',
      'Change',
      '5.A.1.a Solid Waste Disposal to land1.A.1.a Public Electricity and Heat Production2.A.4 Other Process uses of Carbonates',
      'Change',
    ]);
  });

  it('should render the summary when no editable', () => {
    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(Array.from(element.querySelectorAll('dd')).map((el) => el.textContent.trim())).toEqual([
      'Ammonia production (Carbon dioxide)',
      '100 kVA',
      '5.A.1.a Solid Waste Disposal to land1.A.1.a Public Electricity and Heat Production2.A.4 Other Process uses of Carbonates',
    ]);
  });
});
