import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { VERIFICATION_REPORT } from '@aviation/request-task/aer/ukets/aer-verify/tests/mock-verification-report';
import { SharedModule } from '@shared/shared.module';

import { VerifyEmissionsReductionClaimGroupComponent } from './verify-emissions-reduction-claim-group.component';

describe('VerifyEmissionsReductionClaimGroupComponent', () => {
  let component: VerifyEmissionsReductionClaimGroupComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-verify-emissions-reduction-claim-group
        [isEditable]="isEditable"
        [emissionsReductionClaimVerification]="emissionsReductionClaimVerification"
        [queryParams]="queryParams"
      ></app-verify-emissions-reduction-claim-group>
    `,
  })
  class TestComponent {
    isEditable = false;
    emissionsReductionClaimVerification = VERIFICATION_REPORT.emissionsReductionClaimVerification;
    queryParams = {};
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
    component = fixture.debugElement.query(By.directive(VerifyEmissionsReductionClaimGroupComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the verify emissions reduction claim groups without being editable', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLDListElement>('dl')).map((dl) => [
        Array.from(dl.querySelectorAll('dt')).map((el) => el.textContent.trim()),
        Array.from(dl.querySelectorAll('dd')).map((el) => el.textContent.trim()),
      ]),
    ).toEqual([
      [
        [
          'Have you reviewed all the aircraft operator’s sustainable aviation fuel (SAF) batch claims?',
          'Results of your review',
          'Confirmation of no double-counting',
          'Do all of the batch claims reviewed contain evidence that shows the sustainability, purchase and delivery criteria were met?',
          'Compliance with UK ETS requirements',
        ],
        ['Yes', 'reviewResults', 'noDoubleCountingConfirmation', 'Yes', 'Yes'],
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
          'Have you reviewed all the aircraft operator’s sustainable aviation fuel (SAF) batch claims?',
          'Results of your review',
          'Confirmation of no double-counting',
          'Do all of the batch claims reviewed contain evidence that shows the sustainability, purchase and delivery criteria were met?',
          'Compliance with UK ETS requirements',
        ],
        [
          'Yes',
          'Change',
          'reviewResults',
          'Change',
          'noDoubleCountingConfirmation',
          'Change',
          'Yes',
          'Change',
          'Yes',
          'Change',
        ],
      ],
    ]);
  });
});
