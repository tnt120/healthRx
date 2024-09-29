import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Conversation } from '../../models/conversation.model';
import { ChatMessageDto } from '../../models/chat-message-dto.model';
import { Sex } from '../../enums/sex.enum';
import { Roles } from '../../enums/roles.enum';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly apiUrl = `${environment.apiUrl}/chat`;

  private readonly http = inject(HttpClient);

  private conversationsSubject = new BehaviorSubject<Conversation[]>([]);

  conversations$ = this.conversationsSubject.asObservable();

  getConversations(): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${this.apiUrl}/conversations`).pipe(
      tap(res => {
        this.conversationsSubject.next(res);
      })
    );
  }

  getMessages(friendshipId: string): Observable<ChatMessageDto[]> {
    return this.http.get<ChatMessageDto[]>(`${this.apiUrl}/messages/${friendshipId}`).pipe(
      // tap(res => {
      //   this.updateConversationMessages(friendshipId, res, 'addMulipleMessages');
      // })
    );
  }

  updateConversationMessages(friendshipId: string, messages: ChatMessageDto[] | null = null, action: 'addSingleMessage' | 'addMulipleMessages'): void {
    const currentConversations = this.conversationsSubject.getValue();

    switch (action) {
      case 'addSingleMessage':
        if (messages && messages.length === 1 && currentConversations) {
          const message = messages[0];
          const findConversation = currentConversations.find(c => c.friendshipId === friendshipId);

          if (findConversation) {
            findConversation.messages?.push(message);
            findConversation.lastMessage = message;
            const newConversations = currentConversations.map(c => {
              if (c.friendshipId === friendshipId) {
                return findConversation;
              }
              return c;
            });

            this.conversationsSubject.next(newConversations);
          } else {
            // todo add new conversation
          }

        }
        break;
      case 'addMulipleMessages':
        if (messages && currentConversations) {
          const newConversations = currentConversations.map(c => {
            if (c.friendshipId === friendshipId) {
              c.messages = messages;
              c.lastMessage = messages[messages.length - 1];
              return c;
            } else {
              return c;
            }

            this.conversationsSubject.next(newConversations);
          })
        }

        break;
    }
  }
}
