import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { FormArray, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TextInputComponent } from '../text-input/text-input.component';
import { FormService } from './form.service';

describe('FormService', () => {
  let service: FormService;

  @Component({
    imports: [ReactiveFormsModule, FormsModule, TextInputComponent],
    template: `
      <div [formGroup]="formGroup">
        <div govuk-text-input formControlName="test"></div>
        <div formArrayName="list">
          @for (control of list.controls; track control) {
            <div govuk-text-input [formControlName]="$index"></div>
          }
        </div>
      </div>

      <form #ngForm>
        <div govuk-text-input [(ngModel)]="model" name="someField"></div>
      </form>
    `,
  })
  class TestComponent {
    formGroup = new FormGroup({
      test: new FormControl(),
      list: new FormArray([new FormControl()]),
    });
    model: string;

    get list(): FormArray {
      return this.formGroup.get('list') as FormArray;
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, FormsModule, TextInputComponent, TestComponent],
    });
    service = TestBed.inject(FormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create an id from the control path', () => {
    const fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('#test')).toBeTruthy();
    expect(element.querySelector('#list\\.0')).toBeTruthy();
    expect(element.querySelector('#someField')).toBeTruthy();
  });
});
