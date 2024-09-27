import { Component, inject, OnDestroy, OnInit } from '@angular/core';

@Component({
  selector: 'app-user-messages',
  templateUrl: './user-messages.component.html',
  styleUrl: './user-messages.component.scss'
})
export class UserMessagesComponent implements OnInit, OnDestroy {
  ngOnInit(): void {
    // this.websocketService.connectToChat();
  }

  ngOnDestroy(): void {
    // this.websocketService.disconnectFromChat();
  }
}
