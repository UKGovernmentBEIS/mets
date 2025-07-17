import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { LegalEntitiesService, LegalEntityDTO } from 'pmrv-api';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { LegalEntityHelperService } from './legal-entity-helper.service';

describe('LegalEntityHelperService', () => {
  let service: LegalEntityHelperService;
  let legalEntitiesService: jest.Mocked<LegalEntitiesService>;
  let store: jest.Mocked<InstallationAccountApplicationStore>;

  beforeEach(() => {
    const legalEntitiesServiceMock = {
      getLegalEntityById: jest.fn(),
    };

    const storeMock = {
      updateTask: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        LegalEntityHelperService,
        { provide: LegalEntitiesService, useValue: legalEntitiesServiceMock },
        { provide: InstallationAccountApplicationStore, useValue: storeMock },
      ],
    });

    service = TestBed.inject(LegalEntityHelperService);
    legalEntitiesService = TestBed.inject(LegalEntitiesService) as jest.Mocked<LegalEntitiesService>;
    store = TestBed.inject(InstallationAccountApplicationStore) as jest.Mocked<InstallationAccountApplicationStore>;
  });

  describe('getLegalEntities', () => {
    it('should map legal entities to select options', () => {
      const route = {
        data: of({
          legalEntities: [
            { id: 1, name: 'Entity 1' },
            { id: 2, name: 'Entity 2' },
          ],
        }),
      } as unknown as ActivatedRoute;
      const expectedOptions = [
        { text: 'Entity 1', value: 1 },
        { text: 'Entity 2', value: 2 },
      ];

      service.getLegalEntities(route).subscribe((options) => {
        expect(options).toEqual(expectedOptions);
      });
    });

    it('should update legal entity details in the store', (done) => {
      const id = 1;
      const selectGroupFormValue = { isNew: false };

      const legalEntity: LegalEntityDTO = {
        id: 1,
        type: 'PARTNERSHIP',
        name: 'test',
        referenceNumber: 'ab123456',
        noReferenceNumberReason: '',
        address: {
          line1: 'line1',
          line2: 'line2',
          city: 'city',
          country: 'GR',
          postcode: 'postcode',
        },
        holdingCompany: {
          name: 'testhc',
          registrationNumber: '999999999',
          address: {
            line1: 'hcline1',
            line2: null,
            city: 'hccity',
            postcode: 'hcpostcode',
          },
        },
      };

      legalEntitiesService.getLegalEntityById.mockReturnValue(of(legalEntity));

      service.updateLegalEntityDetails(id, selectGroupFormValue).subscribe(() => {
        expect(store.updateTask).toHaveBeenCalledWith(
          ApplicationSectionType.legalEntity,
          {
            selectGroup: selectGroupFormValue,
            detailsGroup: {
              address: legalEntity.address,
              name: legalEntity.name,
              type: legalEntity.type,
              referenceNumber: legalEntity.referenceNumber,
              noReferenceNumberReason: legalEntity.noReferenceNumberReason,
              belongsToHoldingCompany: !!legalEntity.holdingCompany,
            },
          },
          'complete',
        );
        done();
      });
    });
  });
});
