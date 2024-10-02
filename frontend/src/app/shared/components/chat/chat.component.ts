import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { WebsocketService } from '../../../core/services/websocket/websocket.service';
import { ChatService } from '../../../core/services/chat/chat.service';
import { Conversation } from '../../../core/models/conversation.model';
import { UserResponse } from '../../../core/models/user/user-response.model';
import { Observable, Subscription, tap } from 'rxjs';
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

  chatUser$: Observable<UserResponse> = this.store.select('user').pipe(tap(user => this.chatUser = user));

  chatUser: UserResponse | null = null;

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
        if (this.selectedConversation?.friendshipId === message.friendshipId && !message.isRead && this.chatUser && message.receiverId === this.chatUser.id) {
          this.websocketService.sendReadReceipt(message);
          message.isRead = true;
        }

        this.conversations = this.conversations.map(c => {
          if (c.friendshipId === message.friendshipId) {
            c.messages = [message, ...c.messages || []];
            c.lastMessage = message;
          }
          return c;
        });


        this.sortFriendships();
      })
    );

    this.subscriptions.push(
      this.websocketService.messageRead$.subscribe(message => {
        const findedConversation = this.conversations.find(c => c.friendshipId === message.friendshipId);

        if (findedConversation) {
          const findedMessage = findedConversation.messages?.find(m => m.id === message.id);

          if (findedMessage) {
            findedMessage.isRead = true;
          }
        }
      })
    )
  }

  ngOnDestroy(): void {
    this.websocketService.disconnectFromChat();

    this.subscriptions.forEach(s => s.unsubscribe());
  }

  sendMessage(req: { content: string, friendshipId: string, receiverId: string, senderId: string }) {
    this.websocketService.sendChatMessage(req.content, req.friendshipId, req.receiverId);
  }

  onConversationSelect(conversation: Conversation) {
    this.selectedConversation = conversation;
    this.chatService.getMessages(conversation.friendshipId).subscribe(res => {
      this.conversations = this.conversations.map(c => {
        if (c.friendshipId === conversation.friendshipId) {
          res = res.map(message => {
            if (!message.isRead && this.chatUser && message.receiverId === this.chatUser.id) {
              this.websocketService.sendReadReceipt(message);
              message.isRead = true
            }

            return message;
          });

          c.messages = [...res];

          if (c.lastMessage) c.lastMessage.isRead = true;

          this.selectedConversation = c;
        }
        return c;
      })
    });
  }

  sortFriendships(): void {
    this.conversations = [...this.conversations.sort((a, b) => {
      const dateA = a?.lastMessage?.createdAt ? new Date(a.lastMessage.createdAt).getTime() : 0;
      const dateB = b?.lastMessage?.createdAt ? new Date(b.lastMessage.createdAt).getTime() : 0;
      return dateB - dateA;
    })];
  }
}
