import { Component, ViewChild } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { FormService } from 'govuk-components';

import { CsvDataErrorSummaryComponent } from './csv-data-error-summary.component';

describe('CsvDataErrorSummaryComponent', () => {
  @Component({
    template: ` <app-csv-data-error-summary></app-csv-data-error-summary> `,
  })
  class TestHostComponent {
    @ViewChild(CsvDataErrorSummaryComponent) component: CsvDataErrorSummaryComponent;
  }

  let component: CsvDataErrorSummaryComponent;
  let hostComponent: TestHostComponent;
  let fixture: ComponentFixture<TestHostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule, SharedModule, CsvDataErrorSummaryComponent],
      declarations: [TestHostComponent],
      providers: [FormService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();
    component = hostComponent.component;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
