import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { ApproachesDeleteTemplateComponent } from '@shared/components/approaches/approaches-delete/approaches-delete-template.component';
import { SharedModule } from '@shared/shared.module';

import { PermitMonitoringApproachSection } from 'pmrv-api';

describe('ApproachesDeleteTemplateComponent', () => {
  let component: ApproachesDeleteTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-approaches-delete-template
        (delete)="onDelete()"
        [monitoringApproach]="monitoringApproach"></app-approaches-delete-template>
    `,
  })
  class TestComponent {
    monitoringApproach = 'CALCULATION_CO2' as PermitMonitoringApproachSection['type'];
    onDelete: () => any | jest.SpyInstance<void>;
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
    component = fixture.debugElement.query(By.directive(ApproachesDeleteTemplateComponent)).componentInstance;
    hostComponent.onDelete = jest.fn();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the remove button and approach', () => {
    expect(element.querySelectorAll<HTMLButtonElement>('button[govukwarnbutton]').length).toEqual(1);
    expect(element.querySelector('h1').textContent.trim()).toEqual(
      "Are you sure you want to delete'Calculation of CO2'?",
    );
  });
});
