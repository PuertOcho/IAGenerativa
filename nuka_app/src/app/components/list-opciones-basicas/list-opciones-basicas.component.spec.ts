import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { ListOpcionesBasicasComponent } from './list-opciones-basicas.component';

describe('ListOpcionesBasicasComponent', () => {
  let component: ListOpcionesBasicasComponent;
  let fixture: ComponentFixture<ListOpcionesBasicasComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ListOpcionesBasicasComponent ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(ListOpcionesBasicasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
