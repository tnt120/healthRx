import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatConversationsTabComponent } from './chat-conversations-tab.component';

describe('ChatConversationsTabComponent', () => {
  let component: ChatConversationsTabComponent;
  let fixture: ComponentFixture<ChatConversationsTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChatConversationsTabComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatConversationsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
