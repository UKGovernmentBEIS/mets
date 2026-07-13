import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagComponent } from './tag.component';
import { TagColor } from './tag-color.type';

describe('TagComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    imports: [TagComponent],
    template: `
      <govuk-tag [color]="color" />
    `,
  })
  class TestComponent {
    color = 'green' as TagColor;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TagComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be in green color', () => {
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    element.querySelector<HTMLElement>('.govuk-tag');

    expect(element.querySelector<HTMLElement>('.govuk-tag').classList).toContain('govuk-tag--green');
  });
});
