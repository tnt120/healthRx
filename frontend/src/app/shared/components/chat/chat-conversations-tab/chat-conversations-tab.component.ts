import { Component, input, output } from '@angular/core';
import { Conversation } from '../../../../core/models/conversation.model';

@Component({
  selector: 'app-chat-conversations-tab',
  templateUrl: './chat-conversations-tab.component.html',
  styleUrl: './chat-conversations-tab.component.scss'
})
export class ChatConversationsTabComponent {
  conversations = input.required<Conversation[]>();

  selectEmit = output<Conversation>();

  selectedConversation: Conversation | null = null;

  onConversationSelect(conversation: Conversation) {
    this.selectedConversation = conversation;
    this.selectEmit.emit(conversation);
  }
}
