import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { PrtrSummaryTemplateComponent } from '@shared/components/prtr/prtr-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { PRTRCodes } from 'pmrv-api';

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
    activities: PRTRCodes = {
      exist: true,
      codes: ['_1_A_MINERAL_OIL_GAS_REFINERIES', '_1_B_INSTALLATIONS_FOR_GASIFICATION_LIGUEFACTION'],
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
      'Main activity1.(a) Mineral oil and gas refineriesDelete',
      'Main activity1.(b) Installations for gasification and liquefactionDelete',
    ]);
    expect(element.querySelector('h2').textContent.trim()).toEqual('EPRTR codes added');
    expect(element.querySelector('button').textContent.trim()).toEqual('Add another');

    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(element.querySelector('dl')).toBeFalsy();
    expect(Array.from(element.querySelectorAll('tr')).map((el) => el.textContent.trim())).toEqual([
      '',
      'Main activity1.(a) Mineral oil and gas refineries',
      'Main activity1.(b) Installations for gasification and liquefaction',
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
