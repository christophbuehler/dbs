import { Component, OnInit } from '@angular/core';
import { ApiService, Uni } from '../shared/api.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  unis: Observable<Uni[]>;

  constructor(
    public api: ApiService,
  ) {
    this.unis = api.unis;
  }

  ngOnInit(): void {
  }
}
