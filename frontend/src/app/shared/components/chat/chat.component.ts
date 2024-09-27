import { Component, inject, input } from '@angular/core';
import { WebsocketService } from '../../../core/services/websocket/websocket.service';
import { ChatService } from '../../../core/services/chat/chat.service';
import { Conversation } from '../../../core/models/conversation.model';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent {
  private readonly websocketService = inject(WebsocketService);

  private readonly chatService = inject(ChatService);

  conversations: Conversation[] = [];

  selectedConversation: Conversation | null = null;

  ngOnInit(): void {
    this.websocketService.connectToChat();

    this.chatService.getConversations().subscribe(res => {
      this.conversations = [...res];
    });
  }

  ngOnDestroy(): void {
    this.websocketService.disconnectFromChat();
  }

  sendMessage() {
    // this.websocketService.sendChatMessage(this.message, this.friendshipId(), this.receiverId());
  }

  onConversationSelect(conversation: Conversation) {
    this.selectedConversation = conversation;
    this.chatService.getMessages(conversation.friendshipId).subscribe(res => {
      this.conversations = this.conversations.map(c => {
        if (c.friendshipId === conversation.friendshipId) {
          c.messages = res;
          this.selectedConversation = c;
        }
        return c;
      })
    });
  }
}
