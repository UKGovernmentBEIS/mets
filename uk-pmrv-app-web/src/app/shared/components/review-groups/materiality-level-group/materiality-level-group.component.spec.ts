import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { MaterialityLevelGroupComponent } from '@shared/components/review-groups/materiality-level-group/materiality-level-group.component';
import { SharedModule } from '@shared/shared.module';

import { MaterialityLevel } from 'pmrv-api';

describe('MaterialityLevelGroupComponent', () => {
  let component: MaterialityLevelGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-materiality-level-group
        [isEditable]="isEditable"
        [materialityLevelInfo]="materialityLevelInfo"
      ></app-materiality-level-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    materialityLevelInfo = {
      materialityDetails: 'Materiality details',
      accreditationReferenceDocumentTypes: ['EU_ETS_VERIFICATION_CONDUCT_ACCREDITED_VER_EA_6_03', 'OTHER'],
      otherReference: 'Other type',
    } as MaterialityLevel;
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
    component = fixture.debugElement.query(By.directive(MaterialityLevelGroupComponent)).componentInstance;
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
          `EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive  Other type`,
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
          `EA-6/03 European Co-operation for Accreditation Guidance for the Recognition of Verifiers under EU ETS Directive  Other type`,
          'Change',
        ],
      ],
    ]);
  });
});
