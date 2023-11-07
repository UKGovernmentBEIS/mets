import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { PrtrSummaryTemplateComponent } from '@shared/components/prtr/prtr-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { PollutantRegisterActivities } from 'pmrv-api';

describe('PrtrSummaryTemplateComponent', () => {
  let component: PrtrSummaryTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-prtr-summary-template [activities]="activities" [isEditable]="isEditable"></app-prtr-summary-template>
    `,
  })
  class TestComponent {
    activities: PollutantRegisterActivities = {
      exist: true,
      activities: ['_1_A_2_C_CHEMICALS', '_1_A_3_C_RAILWAYS', '_1_B_2_A_OIL'],
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
    component = fixture.debugElement.query(By.directive(PrtrSummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the list', () => {
    expect(element.querySelector('dl')).toBeFalsy();
    expect(Array.from(element.querySelectorAll('tr')).map((el) => el.textContent.trim())).toEqual([
      '',
      'Main activity1.A.2.c ChemicalsDelete',
      'Main activity1.A.3.c RailwaysDelete',
      'Main activity1.B.2.a OilDelete',
    ]);
    expect(element.querySelector('h2').textContent.trim()).toEqual('EPRTR codes added');
    expect(element.querySelector('button').textContent.trim()).toEqual('Add another');

    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(element.querySelector('dl')).toBeFalsy();
    expect(Array.from(element.querySelectorAll('tr')).map((el) => el.textContent.trim())).toEqual([
      '',
      'Main activity1.A.2.c Chemicals',
      'Main activity1.A.3.c Railways',
      'Main activity1.B.2.a Oil',
    ]);
    expect(element.querySelector('h2')).toBeFalsy();
    expect(element.querySelector('button')).toBeFalsy();
  });

  it('should not render the list', () => {
    hostComponent.activities = { exist: false };
    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll('dl')).map((el) => [
        el.querySelector('dt').textContent.trim(),
        Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent),
      ]),
    ).toEqual([
      [
        'Are emissions from the installation reported under the Pollutant Release and Transfer Register?',
        ['No', 'Change'],
      ],
    ]);
    expect(element.querySelector('tr')).toBeFalsy();
    expect(element.querySelector('h2')).toBeFalsy();
    expect(element.querySelector('button')).toBeFalsy();

    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll('dl')).map((el) => [
        el.querySelector('dt').textContent.trim(),
        Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent),
      ]),
    ).toEqual([
      ['Are emissions from the installation reported under the Pollutant Release and Transfer Register?', ['No']],
    ]);
    expect(element.querySelector('tr')).toBeFalsy();
    expect(element.querySelector('h2')).toBeFalsy();
    expect(element.querySelector('button')).toBeFalsy();
  });
});
