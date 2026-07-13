import { Component, viewChildren } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { TextInputComponent } from '../../text-input';
import { ConditionalContentDirective } from './conditional-content.directive';

describe('ConditionalContentDirective', () => {
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;

  @Component({
    imports: [ReactiveFormsModule, ConditionalContentDirective, TextInputComponent],
    template: `
      <form [formGroup]="form">
        <div govukConditionalContent>
          <div govuk-text-input formControlName="first"></div>

          <div govukConditionalContent>
            <div govuk-text-input formControlName="second"></div>
            <div formGroupName="nestedGroup"></div>
          </div>

          <div formGroupName="group">
            <div govukConditionalContent>
              <div govuk-text-input formControlName="third"></div>
            </div>
            <div govuk-text-input formControlName="fourth"></div>
          </div>
        </div>
      </form>
    `,
  })
  class TestComponent {
    readonly conditionals = viewChildren(ConditionalContentDirective);

    form = new FormGroup({
      first: new FormControl(),
      second: new FormControl(),
      group: new FormGroup({ third: new FormControl(), fourth: new FormControl() }),
      nestedGroup: new FormGroup({}),
    });
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(fixture.debugElement.queryAll(By.directive(ConditionalContentDirective))).toHaveLength(3);
  });

  it('should disable all nested controls and groups', () => {
    hostComponent.conditionals().at(0).disableControls();
    fixture.detectChanges();

    expect(hostComponent.form.get('first').disabled).toBeTruthy();
    expect(hostComponent.form.get('second').disabled).toBeTruthy();
    expect(hostComponent.form.get(['group', 'third']).disabled).toBeTruthy();
    expect(hostComponent.form.get(['group', 'fourth']).disabled).toBeTruthy();
    expect(hostComponent.form.get('group').disabled).toBeTruthy();
    expect(hostComponent.form.get('nestedGroup').disabled).toBeTruthy();
  });

  it('should enable nested controls', () => {
    hostComponent.form.disable();
    fixture.detectChanges();

    hostComponent.conditionals().at(0).enableControls();
    fixture.detectChanges();

    expect(hostComponent.form.get('first').disabled).toBeFalsy();
    expect(hostComponent.form.get('second').disabled).toBeTruthy();
    expect(hostComponent.form.get('group').disabled).toBeFalsy();
    expect(hostComponent.form.get(['group', 'third']).disabled).toBeTruthy();
    expect(hostComponent.form.get(['group', 'fourth']).disabled).toBeFalsy();
    expect(hostComponent.form.get('nestedGroup').disabled).toBeTruthy();

    hostComponent.conditionals().at(1).enableControls();
    fixture.detectChanges();

    expect(hostComponent.form.get('second').disabled).toBeFalsy();
    expect(hostComponent.form.get(['group', 'third']).disabled).toBeTruthy();
  });
});
