import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrService } from '@tasks/bdr/shared';

import { BdrCompleteConfirmationComponent } from './bdr-complete-confirmation.component';

describe('BdrCompleteConfirmationComponent', () => {
  let component: BdrCompleteConfirmationComponent;
  let fixture: ComponentFixture<BdrCompleteConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrCompleteConfirmationComponent],
      providers: [BdrService, ItemNamePipe, provideRouter([]), CapitalizeFirstPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(BdrCompleteConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
