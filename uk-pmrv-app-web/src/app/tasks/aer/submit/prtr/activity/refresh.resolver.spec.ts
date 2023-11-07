import { TestBed } from '@angular/core/testing';

import { RefreshResolver } from './refresh.resolver';

describe('RefreshResolver', () => {
  let resolver: RefreshResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    resolver = TestBed.inject(RefreshResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
