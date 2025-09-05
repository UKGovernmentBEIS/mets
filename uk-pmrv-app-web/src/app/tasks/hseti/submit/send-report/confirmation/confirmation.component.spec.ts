import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { HseTiService } from '@tasks/hseti/core';

import { HseTiSendReportConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: HseTiSendReportConfirmationComponent;
  let fixture: ComponentFixture<HseTiSendReportConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HseTiSendReportConfirmationComponent],
      providers: [HseTiService, ItemNamePipe, provideRouter([]), CapitalizeFirstPipe],
    }).compileComponents();

    fixture = TestBed.createComponent(HseTiSendReportConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
