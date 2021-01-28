import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-post-response',
  templateUrl: './post-response.component.html',
  styleUrls: ['./post-response.component.scss']
})
export class PostResponseComponent implements OnInit {
  @Input() response: any;

  constructor() { }

  ngOnInit(): void {
  }
}
