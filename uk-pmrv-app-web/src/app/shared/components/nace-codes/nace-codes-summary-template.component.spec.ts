import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { NaceCodesSummaryTemplateComponent } from '@shared/components/nace-codes/nace-codes-summary-template.component';
import { SharedModule } from '@shared/shared.module';

import { NaceCodes } from 'pmrv-api';

describe('NaceCodesSummaryTemplateComponent', () => {
  let component: NaceCodesSummaryTemplateComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-nace-codes-summary-template
        [naceCodes]="naceCodes"
        [isEditable]="isEditable"
      ></app-nace-codes-summary-template>
    `,
  })
  class TestComponent {
    naceCodes: NaceCodes = {
      codes: ['_1011_PROCESSING_AND_PRESERVING_OF_MEAT', '_1012_PROCESSING_AND_PRESERVING_OF_POULTRY_MEAT'],
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
    component = fixture.debugElement.query(By.directive(NaceCodesSummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the table', () => {
    expect(
      Array.from(element.querySelectorAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      ),
    ).toEqual([
      [],
      ['Main activity', '1011 Processing and preserving of meat', 'Delete'],
      ['Main activity', '1012 Processing and preserving of poultry meat', 'Delete'],
    ]);

    hostComponent.isEditable = false;
    fixture.detectChanges();

    expect(
      Array.from(element.querySelectorAll('tr')).map((el) =>
        Array.from(el.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      ),
    ).toEqual([
      [],
      ['Main activity', '1011 Processing and preserving of meat', ''],
      ['Main activity', '1012 Processing and preserving of poultry meat', ''],
    ]);
  });
});
