import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { FieldsetHintDirective } from './fieldset-hint.directive';

describe('FieldsetHintDirective', () => {
  let directive: FieldsetHintDirective;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    imports: [FieldsetHintDirective],
    template: '<div govukFieldsetHint>This is a hint</div>',
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FieldsetHintDirective, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    directive = fixture.debugElement.query(By.directive(FieldsetHintDirective)).componentInstance;
    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should have the hint class', () => {
    const hintElement: HTMLSpanElement = fixture.debugElement.query(By.directive(FieldsetHintDirective)).nativeElement;
    expect(hintElement.classList.contains('govuk-hint')).toBeTruthy();
  });
});
