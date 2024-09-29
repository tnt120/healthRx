import { ChatMessageDto } from './../../../core/models/chat-message-dto.model';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { WebsocketService } from '../../../core/services/websocket/websocket.service';
import { ChatService } from '../../../core/services/chat/chat.service';
import { Conversation } from '../../../core/models/conversation.model';
import { UserResponse } from '../../../core/models/user/user-response.model';
import { Observable, Subscription } from 'rxjs';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit, OnDestroy {
  private readonly websocketService = inject(WebsocketService);

  private readonly chatService = inject(ChatService);

  private readonly store = inject(Store);

  conversations: Conversation[] = [];

  selectedConversation: Conversation | null = null;

  chatUser$: Observable<UserResponse> = this.store.select('user');

  subscriptions: Subscription[] = [];

  ngOnInit(): void {
    this.websocketService.connectToChat();

    this.subscriptions.push(
      this.chatService.getConversations().subscribe(res => {
        this.conversations = [...res];
      })
    );

    this.subscriptions.push(
      this.websocketService.messageReceived$.subscribe(message => {
        this.conversations = this.conversations.map(c => {
          if (c.friendshipId === message.friendshipId) {
            c.messages = [message, ...c.messages || []];
            c.lastMessage = message;
          }
          return c;
        });

        this.sortFriendships();
      })
    )
  }

  ngOnDestroy(): void {
    this.websocketService.disconnectFromChat();

    this.subscriptions.forEach(s => s.unsubscribe());
  }

  sendMessage(req: { content: string, friendshipId: string, receiverId: string, senderId: string }) {
    this.websocketService.sendChatMessage(req.content, req.friendshipId, req.receiverId);

    const [date, time] = new Date().toLocaleString('pl-PL', { timeZone: 'Europe/Warsaw' }).replace(',', '').split(' ');
    const [day, month, year] = date.split('.');
    const [hours, minutes, seconds] = time.split(':');

    const parsedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(date), parseInt(hours), parseInt(minutes), parseInt(seconds)).toISOString();

    const message: ChatMessageDto = {
      senderId: req.senderId,
      receiverId: req.receiverId,
      content: req.content,
      friendshipId: req.friendshipId,
      createdAt: parsedDate,
      isDelivered: false,
      isRead: false
    };

    if (this.selectedConversation?.friendshipId === req.friendshipId) {
      this.conversations.map(c => {
        if (c.friendshipId === req.friendshipId) {
          c.messages = [message, ...c.messages || []];
          c.lastMessage = message;
        }
        return c;
      })
      this.sortFriendships();
    }
  }

  onConversationSelect(conversation: Conversation) {
    this.selectedConversation = conversation;
    this.chatService.getMessages(conversation.friendshipId).subscribe(res => {
      this.conversations = this.conversations.map(c => {
        if (c.friendshipId === conversation.friendshipId) {
          c.messages = [...res];
          this.selectedConversation = c;
        }
        return c;
      })
    });
  }

  sortFriendships(): void {
    this.conversations = [...this.conversations.sort((a, b) => {
      const dateA = a.lastMessage.createdAt ? new Date(a.lastMessage.createdAt).getTime() : 0;
      const dateB = b.lastMessage.createdAt ? new Date(b.lastMessage.createdAt).getTime() : 0;
      return dateB - dateA;
    })];
  }
}
