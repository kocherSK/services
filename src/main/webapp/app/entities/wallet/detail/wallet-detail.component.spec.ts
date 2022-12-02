import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WalletDetailComponent } from './wallet-detail.component';

describe('Wallet Management Detail Component', () => {
  let comp: WalletDetailComponent;
  let fixture: ComponentFixture<WalletDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WalletDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ wallet: { id: 'ABC' } }) },
        },
      ],
    })
      .overrideTemplate(WalletDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(WalletDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load wallet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.wallet).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
