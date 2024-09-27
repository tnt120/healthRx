export interface ChatMessageDto {
  id?: string;
  senderId: string;
  receiverId: string;
  content: string;
  friendshipId: string;
  createdAt?: string;
  isDelivered?: boolean;
  isRead?: boolean;
}
