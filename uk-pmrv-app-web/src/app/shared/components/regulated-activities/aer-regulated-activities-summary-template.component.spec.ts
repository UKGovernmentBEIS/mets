import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { AerRegulatedActivitiesSummaryTemplateComponent } from '@shared/components/regulated-activities/aer-regulated-activities-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { AerRegulatedActivity } from 'pmrv-api';

describe('RegulatedActivitiesSummaryTemplateComponent', () => {
  let component: AerRegulatedActivitiesSummaryTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-aer-regulated-activities-summary-template
        [activity]="activity"
        [isEditable]="isEditable"
      ></app-aer-regulated-activities-summary-template>
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
      energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
      industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
    };
    isEditable = true;
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
      '1.A.1.a Public Electricity and Heat Production2.A.4 Other Process uses of Carbonates',
      'Change',
    ]);
  });

  it('should render the summary when no editable', () => {
    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(Array.from(element.querySelectorAll('dd')).map((el) => el.textContent.trim())).toEqual([
      'Ammonia production (Carbon dioxide)',
      '100 kVA',
      '1.A.1.a Public Electricity and Heat Production2.A.4 Other Process uses of Carbonates',
    ]);
  });
});
