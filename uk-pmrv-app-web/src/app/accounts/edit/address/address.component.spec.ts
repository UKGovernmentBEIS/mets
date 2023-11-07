import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroupName } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { InstallationAccountUpdateService, LocationOffShoreDTO, LocationOnShoreDTO } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../../../testing';
import { AddressInputComponent } from '../../../shared/address-input/address-input.component';
import { SharedModule } from '../../../shared/shared.module';
import { mockedAccountPermit, mockedOffshoreAccountPermit } from '../../testing/mock-data';
import { AddressComponent } from './address.component';

describe('AddressComponent', () => {
  let component: AddressComponent;
  let fixture: ComponentFixture<AddressComponent>;
  let accountUpdateService: MockType<InstallationAccountUpdateService>;
  let activatedRoute;
  let page: Page;

  class Page extends BasePage<AddressComponent> {
    set gridReferenceValue(value: string) {
      this.setInputValue('[name="gridReference"]', value);
    }

    set latitudeDegreeValue(value: number) {
      this.setInputValue('[name="latitude.degree"]', value);
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(AddressComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  const createModule = async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [AddressComponent],
      providers: [
        { provide: InstallationAccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    })
      .overrideComponent(AddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  };

  describe('onshore', () => {
    beforeEach(async () => {
      accountUpdateService = {
        updateInstallationAccountAddress: jest.fn().mockReturnValue(asyncData(null)),
      };

      activatedRoute = new ActivatedRouteStub(undefined, undefined, {
        accountPermit: mockedAccountPermit,
      });
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not navigate until  form is valid', () => {
      const location = mockedAccountPermit.account.location as LocationOnShoreDTO;

      page.gridReferenceValue = '';
      page.confirmButton.click();
      fixture.detectChanges();
      expect(accountUpdateService.updateInstallationAccountAddress).not.toHaveBeenCalled();

      page.gridReferenceValue = location.gridReference;
      page.confirmButton.click();

      expect(accountUpdateService.updateInstallationAccountAddress).toHaveBeenCalled();
      expect(accountUpdateService.updateInstallationAccountAddress).toHaveBeenCalledWith(
        mockedAccountPermit.account.id,
        location,
      );
    });
  });

  describe('offshore', () => {
    beforeEach(async () => {
      accountUpdateService = {
        updateInstallationAccountAddress: jest.fn().mockReturnValue(asyncData(null)),
      };

      activatedRoute = new ActivatedRouteStub(undefined, undefined, {
        accountPermit: mockedOffshoreAccountPermit,
      });
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not navigate until  form is valid', () => {
      const location = mockedOffshoreAccountPermit.account.location as LocationOffShoreDTO;
      page.latitudeDegreeValue = undefined;

      page.confirmButton.click();
      expect(accountUpdateService.updateInstallationAccountAddress).not.toHaveBeenCalled();

      page.latitudeDegreeValue = location.latitude.degree;
      page.confirmButton.click();

      expect(accountUpdateService.updateInstallationAccountAddress).toHaveBeenCalled();
      expect(accountUpdateService.updateInstallationAccountAddress).toHaveBeenCalledWith(
        mockedOffshoreAccountPermit.account.id,
        location,
      );
    });
  });
});
