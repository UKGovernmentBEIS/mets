import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { VERIFICATION_REPORT } from '../../../../request-task/aer/ukets/aer-verify/tests/mock-verification-report';
import { DataGapsMethodologiesGroupComponent } from './data-gaps-methodologies-group.component';

describe('DataGapsMethodologiesGroupComponent', () => {
  let component: DataGapsMethodologiesGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-data-gaps-methodologies-group
        [isEditable]="isEditable"
        [dataGapsMethodologies]="dataGapsMethodologies"
        [queryParams]="queryParams"></app-data-gaps-methodologies-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    dataGapsMethodologies = VERIFICATION_REPORT.dataGapsMethodologies;
    queryParams = {};
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, DataGapsMethodologiesGroupComponent],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(DataGapsMethodologiesGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups without being editable', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Was a data gap method required during the reporting year?',
          'Has the data gap method already been approved by the regulator?',
          'Was the method used conservative?',
          'Did the method lead to a material misstatement?',
        ],
        ['Yes', 'No', 'No  111', 'Yes  222'],
      ],
    ]);
  });

  it('should render the review groups and be editable', () => {
    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Was a data gap method required during the reporting year?',
          'Has the data gap method already been approved by the regulator?',
          'Was the method used conservative?',
          'Did the method lead to a material misstatement?',
        ],
        ['Yes', 'Change', 'No', 'Change', 'No  111', 'Change', 'Yes  222', 'Change'],
      ],
    ]);
  });
});
