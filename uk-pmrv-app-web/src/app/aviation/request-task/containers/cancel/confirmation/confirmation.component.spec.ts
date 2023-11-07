import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [ConfirmationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the confirmation messages correctly', () => {
    const panelTitle = fixture.debugElement.nativeElement.querySelector('.govuk-panel__title').innerHTML.trim();
    const bodyMessage = fixture.debugElement.nativeElement.querySelector('.govuk-body').innerHTML.trim();

    expect(panelTitle).toBe('This task has been cancelled');
    expect(bodyMessage).toBe('It has been removed from your task dashboard.');
  });
});
