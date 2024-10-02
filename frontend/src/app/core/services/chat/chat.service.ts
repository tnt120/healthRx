import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Conversation } from '../../models/conversation.model';
import { ChatMessageDto } from '../../models/chat-message-dto.model';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private readonly apiUrl = `${environment.apiUrl}/chat`;

  private readonly http = inject(HttpClient);

  getConversations(): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${this.apiUrl}/conversations`);
  }

  getMessages(friendshipId: string): Observable<ChatMessageDto[]> {
    return this.http.get<ChatMessageDto[]>(`${this.apiUrl}/messages/${friendshipId}`);
  }
}
