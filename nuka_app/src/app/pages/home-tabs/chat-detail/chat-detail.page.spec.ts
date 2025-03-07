import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { ChatDetailPage } from './chat-detail.page';

describe('ChatDetailPage', () => {
  let component: ChatDetailPage;
  let fixture: ComponentFixture<ChatDetailPage>;

  beforeEach((() => {
    TestBed.configureTestingModule({
      declarations: [ ChatDetailPage ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(ChatDetailPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
