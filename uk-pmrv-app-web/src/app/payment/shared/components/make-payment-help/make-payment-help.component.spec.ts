import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MakePaymentHelpComponent } from './make-payment-help.component';

describe('MakePaymentHelpComponent', () => {
  let component: TestWrapperComponent;
  let fixture: ComponentFixture<TestWrapperComponent>;
  let hostElement: HTMLElement;

  @Component({
    selector: 'app-test-wrapper-component',
    standalone: false,
    template: `
      <app-make-payment-help [defaultHelp]="defaultHelp"></app-make-payment-help>
    `,
  })
  class TestWrapperComponent {
    defaultHelp: string;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestWrapperComponent, MakePaymentHelpComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestWrapperComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the default help text', () => {
    component.defaultHelp = 'default help';
    fixture.detectChanges();
    expect(hostElement.textContent).toContain('default help');
  });

  it('should display nothing when no default help text is provided', () => {
    fixture.detectChanges();
    expect(hostElement.querySelector('p')).toBeNull();
  });
});
