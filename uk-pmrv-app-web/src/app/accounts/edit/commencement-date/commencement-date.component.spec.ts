import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { InstallationAccountUpdateService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { mockedAccountPermit } from '../../testing/mock-data';
import { CommencementDateComponent } from './commencement-date.component';

describe('CommencementDateComponent', () => {
  let component: CommencementDateComponent;
  let fixture: ComponentFixture<CommencementDateComponent>;
  let accountUpdateService: MockType<InstallationAccountUpdateService>;
  let page: Page;
  let route: ActivatedRouteStub;
  class Page extends BasePage<CommencementDateComponent> {
    set commencementDateDay(value: string) {
      this.setInputValue('#commencementDate-day', value);
    }

    set commencementDateMonth(value: string) {
      this.setInputValue('#commencementDate-month', value);
    }

    set commencementDateYear(value: string) {
      this.setInputValue('#commencementDate-year', value);
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub(undefined, undefined, {
      accountPermit: mockedAccountPermit,
    });

    accountUpdateService = {
      updateCommencementDate: jest.fn().mockReturnValue(asyncData(null)),
    };

    await TestBed.configureTestingModule({
      imports: [CommencementDateComponent, RouterTestingModule, SharedModule],
      providers: [
        { provide: InstallationAccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommencementDateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change commencement date on form submit', () => {
    page.commencementDateDay = '10';
    page.commencementDateMonth = '10';
    page.commencementDateYear = '2020';
    fixture.detectChanges();

    page.confirmButton.click();
    fixture.detectChanges();

    expect(accountUpdateService.updateCommencementDate).toHaveBeenCalled();
    expect(accountUpdateService.updateCommencementDate).toHaveBeenCalledWith(1, {
      commencementDate: new Date('2020-10-10'),
    });
  });
});
