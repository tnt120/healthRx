import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrl: './verification.component.scss'
})
export class VerificationComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  verficationToken = '';

  ngOnInit(): void {
    this.verficationToken = this.route.snapshot.paramMap.get('token') as string;
  }
}
