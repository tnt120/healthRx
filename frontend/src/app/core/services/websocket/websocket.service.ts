import { ChatMessageDto } from './../../models/chat-message-dto.model';
import { BehaviorSubject, map, Observable, Subject, tap } from 'rxjs';
import { inject, Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private store = inject(Store);

  private stompClientMessages: Stomp.Client;

  private chatSubscription: Stomp.Subscription | null = null;

  private readMessageSubscription: Stomp.Subscription | null = null;

  private userId = "";

  private messageReceivedSubject = new Subject<ChatMessageDto>();

  private messageReadSubject = new Subject<ChatMessageDto>();

  messageReceived$ = this.messageReceivedSubject.asObservable();

  messageRead$ = this.messageReadSubject.asObservable();

  constructor() {
    this.store.select('user').pipe(tap(user => this.userId = user.id)).subscribe();

    this.stompClientMessages = Stomp.over(new SockJS('http://localhost:8080/ws'));

    this.stompClientMessages.connect({}, () => {
      this.connectToChat();
    });
  }

  connectToChat() {
    if (this.stompClientMessages.connected) {
      this.subscribeToChat();
    } else {
      console.error('Websocket is not connected. Cannot subscribe to chat');
    }
  }

  disconnectFromChat() {
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
      this.chatSubscription = null;
    }

    if (this.readMessageSubscription) {
      this.readMessageSubscription.unsubscribe();
      this.readMessageSubscription = null;
    }
  }

  sendChatMessage(content: string, friendshipId: string, receiverId: string) {
    if (this.stompClientMessages && this.stompClientMessages.connected) {
      const message: ChatMessageDto = {
        senderId: this.userId,
        receiverId: receiverId,
        content: content,
        friendshipId: friendshipId
      };

      this.stompClientMessages.send('/app/chat', {}, JSON.stringify(message));
    } else {
      console.error('Websocket is not connected. Cannot send chat message');
    }
  }

  sendReadReceipt(message: ChatMessageDto) {
    if (this.stompClientMessages && this.stompClientMessages.connected) {
      this.stompClientMessages.send('/app/chat/read', {}, JSON.stringify(message));
    } else {
      console.error('Websocket is not connected. Cannot send read receipt');
    }
  }

  subscribeToChat() {
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
      this.chatSubscription = null;
    }

    if (this.readMessageSubscription) {
      this.readMessageSubscription.unsubscribe();
      this.readMessageSubscription = null;
    }

    this.chatSubscription = this.stompClientMessages.subscribe(`/user/${this.userId}/queue/messages`, (message) => {
      const body: ChatMessageDto = JSON.parse(message.body);
      this.messageReceivedSubject.next(body);
    });

    this.readMessageSubscription = this.stompClientMessages.subscribe(`/user/${this.userId}/queue/messages/read`, (message) => {
      const body: ChatMessageDto = JSON.parse(message.body);
      this.messageReadSubject.next(body);
    });
  }
}
