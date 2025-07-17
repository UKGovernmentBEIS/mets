import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { BdrService } from '@tasks/bdr/shared';

import { BdrSendReportConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: BdrSendReportConfirmationComponent;
  let fixture: ComponentFixture<BdrSendReportConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrSendReportConfirmationComponent],
      providers: [BdrService, ItemNamePipe, provideRouter([]), CapitalizeFirstPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(BdrSendReportConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
