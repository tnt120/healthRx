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

  private stompClient: Stomp.Client;

  private chatSubscription: Stomp.Subscription | null = null;

  private userId = "";

  private messageReceivedBehaviorSubject = new Subject<ChatMessageDto>();

  messageReceived$ = this.messageReceivedBehaviorSubject.asObservable();

  constructor() {
    this.store.select('user').pipe(tap(user => this.userId = user.id)).subscribe();

    this.stompClient = Stomp.over(new SockJS('http://localhost:8080/ws'));

    this.stompClient.connect({}, () => {
      this.connectToChat();
    });
  }

  connectToChat() {
    if (this.stompClient.connected) {
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
  }

  sendChatMessage(content: string, friendshipId: string, receiverId: string) {
    if (this.stompClient && this.stompClient.connected) {
      const message: ChatMessageDto = {
        senderId: this.userId,
        receiverId: receiverId,
        content: content,
        friendshipId: friendshipId
      };

      this.stompClient.send('/app/chat', {}, JSON.stringify(message));
    } else {
      console.error('Websocket is not connected. Cannot send chat message');
    }
  }

  subscribeToChat() {
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
    }
    this.chatSubscription = this.stompClient.subscribe(`/user/${this.userId}/queue/messages`, (message) => {
      const body: ChatMessageDto = JSON.parse(message.body);
      this.messageReceivedBehaviorSubject.next(body);
      console.log('WS message get', body);
    })
  }
}
