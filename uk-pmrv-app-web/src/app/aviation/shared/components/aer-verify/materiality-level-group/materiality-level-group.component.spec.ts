import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { AviationAerMaterialityLevel } from 'pmrv-api';

import { AerVerifyMaterialityLevelGroupComponent } from './materiality-level-group.component';

describe('AerVerifyMaterialityLevelGroupComponent', () => {
  let component: AerVerifyMaterialityLevelGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-aer-verify-materiality-level-group
        [isEditable]="isEditable"
        [materialityLevel]="materialityLevel"
        [queryParams]="queryParams"></app-aer-verify-materiality-level-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    materialityLevel = {
      materialityDetails: 'Materiality details',
      accreditationReferenceDocumentTypes: ['UK_ETS_ACCREDITED_VERIFIERS_AUTHORITY_GUIDANCE', 'OTHER'],
      otherReference: 'Other type',
    } as AviationAerMaterialityLevel;
    queryParams = { change: true };
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerVerifyMaterialityLevelGroupComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AerVerifyMaterialityLevelGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Materiality level', 'Accreditation reference documents'],
        [
          'Materiality details',
          `Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation  Other type`,
        ],
      ],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        ['Materiality level', 'Accreditation reference documents'],
        [
          'Materiality details',
          'Change',
          `Guidance developed by the UK ETS Authority on verification and accreditation in relation to the Monitoring and Reporting Regulation  Other type`,
          'Change',
        ],
      ],
    ]);
  });
});
