import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { DataGapsGroupComponent } from '@shared/components/review-groups/data-gaps-group/data-gaps-group.component';
import { SharedModule } from '@shared/shared.module';

import { MethodologiesToCloseDataGaps } from 'pmrv-api';

describe('DataGapsGroupComponent', () => {
  let component: DataGapsGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  function getSummaryListValues() {
    return Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
      Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
      Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
    ]);
  }

  @Component({
    template: ` <app-data-gaps-group [isEditable]="isEditable" [dataGapsInfo]="dataGapsInfo"></app-data-gaps-group> `,
  })
  class TestComponent {
    isEditable = false;
    dataGapsInfo = {
      dataGapRequired: true,
      dataGapRequiredDetails: {
        dataGapApproved: false,
        dataGapApprovedDetails: {
          conservativeMethodUsed: false,
          methodDetails: 'Methods',
          materialMisstatement: true,
          materialMisstatementDetails: 'Misstatement details',
        },
      },
    } as MethodologiesToCloseDataGaps;
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
    component = fixture.debugElement.query(By.directive(DataGapsGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(getSummaryListValues()).toEqual([
      [
        [
          'Was a data gap method required during the reporting year?',
          'Has the data gap method already been approved by the regulator?',
          'Was the method used conservative?',
          'Provide more detail',
          'Did the method lead to a material misstatement?',
          'Provide more detail',
        ],
        ['Yes', 'No', 'No', 'Methods', 'Yes', 'Misstatement details'],
      ],
    ]);

    hostComponent.isEditable = true;
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      [
        [
          'Was a data gap method required during the reporting year?',
          'Has the data gap method already been approved by the regulator?',
          'Was the method used conservative?',
          'Provide more detail',
          'Did the method lead to a material misstatement?',
          'Provide more detail',
        ],
        [
          'Yes',
          'Change',
          'No',
          'Change',
          'No',
          'Change',
          'Methods',
          'Change',
          'Yes',
          'Change',
          'Misstatement details',
          'Change',
        ],
      ],
    ]);

    hostComponent.isEditable = false;
    hostComponent.dataGapsInfo = {
      dataGapRequired: true,
      dataGapRequiredDetails: {
        dataGapApproved: false,
        dataGapApprovedDetails: {
          conservativeMethodUsed: true,
          materialMisstatement: false,
        },
      },
    };
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      [
        [
          'Was a data gap method required during the reporting year?',
          'Has the data gap method already been approved by the regulator?',
          'Was the method used conservative?',
          'Did the method lead to a material misstatement?',
        ],
        ['Yes', 'No', 'Yes', 'No'],
      ],
    ]);

    hostComponent.dataGapsInfo = {
      dataGapRequired: true,
      dataGapRequiredDetails: {
        dataGapApproved: true,
      },
    };
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([
      [
        [
          'Was a data gap method required during the reporting year?',
          'Has the data gap method already been approved by the regulator?',
        ],
        ['Yes', 'Yes'],
      ],
    ]);

    hostComponent.dataGapsInfo = {
      dataGapRequired: false,
    };
    fixture.detectChanges();

    expect(getSummaryListValues()).toEqual([[['Was a data gap method required during the reporting year?'], ['No']]]);
  });
});
