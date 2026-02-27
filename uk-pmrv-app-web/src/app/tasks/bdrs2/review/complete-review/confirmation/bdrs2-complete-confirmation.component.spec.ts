import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrS2Service } from '@tasks/bdrs2/core';

import { BdrS2CompleteConfirmationComponent } from './bdrs2-complete-confirmation.component';

describe('BdrS2CompleteConfirmationComponent', () => {
  let component: BdrS2CompleteConfirmationComponent;
  let fixture: ComponentFixture<BdrS2CompleteConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrS2CompleteConfirmationComponent],
      providers: [BdrS2Service, ItemNamePipe, provideRouter([]), CapitalizeFirstPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(BdrS2CompleteConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
