import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeTabsPage } from './home-tabs.page';

describe('TabsPage', () => {
  let component: HomeTabsPage;
  let fixture: ComponentFixture<HomeTabsPage>;

  beforeEach((() => {
    TestBed.configureTestingModule({
      declarations: [HomeTabsPage],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeTabsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
