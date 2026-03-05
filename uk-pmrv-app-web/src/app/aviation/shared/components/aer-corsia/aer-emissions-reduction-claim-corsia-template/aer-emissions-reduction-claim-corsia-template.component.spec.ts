import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { AerEmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-corsia/aer-emissions-reduction-claim-corsia-template/aer-emissions-reduction-claim-corsia-template.component';

describe('AerEmissionsReductionClaimCorsiaTemplateComponent', () => {
  let component: AerEmissionsReductionClaimCorsiaTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-aer-emissions-reduction-claim-corsia-template
        [emissionsReductionClaim]="emissionsReductionClaim"
        [cefFiles]="cefFiles"
        [declarationFiles]="declarationFiles"
        [isEditable]="isEditable"></app-aer-emissions-reduction-claim-corsia-template>
    `,
  })
  class TestComponent {
    emissionsReductionClaim = {
      exist: true,
      emissionsReductionClaimDetails: {
        cefFiles: ['randomUUID1'],
        totalEmissions: '1000',
        noDoubleCountingDeclarationFiles: ['randomUUID2'],
      },
    };
    cefFiles = [{ downloadUrl: 'link1', fileName: 'cefFile.png' }];
    declarationFiles = [{ downloadUrl: 'link2', fileName: 'declarationFile.png' }];
    isEditable = true;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerEmissionsReductionClaimCorsiaTemplateComponent, RouterTestingModule],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(
      By.directive(AerEmissionsReductionClaimCorsiaTemplateComponent),
    ).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(summaryListValues()).toEqual([
      ['Will you be making a claim for emissions reductions from the use of CORSIA eligible fuels?', ['Yes', 'Change']],
      ['CEF template', ['cefFile.png', 'Change']],
      ['Total emissions reduction claimed, from the template', ['1000 tonnes CO2', 'Change']],
      ['Declaration of no double claiming', ['declarationFile.png', 'Change']],
    ]);

    hostComponent.emissionsReductionClaim = {
      exist: false,
      emissionsReductionClaimDetails: null,
    };
    hostComponent.cefFiles = [];
    hostComponent.declarationFiles = [];
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Will you be making a claim for emissions reductions from the use of CORSIA eligible fuels?', ['No', 'Change']],
    ]);

    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(summaryListValues()).toEqual([
      ['Will you be making a claim for emissions reductions from the use of CORSIA eligible fuels?', ['No']],
    ]);
  });

  function summaryListValues() {
    return Array.from(element.querySelectorAll<HTMLDivElement>('.govuk-summary-list__row')).map((row) => [
      row.querySelector('dt').textContent.trim(),
      Array.from(row.querySelectorAll('dd')).map((el) => el.textContent.trim()),
    ]);
  }
});
