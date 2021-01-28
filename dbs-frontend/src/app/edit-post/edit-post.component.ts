import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ApiService } from '../shared/api.service';

@Component({
  selector: 'app-edit-post',
  templateUrl: './edit-post.component.html',
  styleUrls: ['./edit-post.component.scss']
})
export class EditPostComponent implements OnInit {
  group = new FormGroup({
    titel: new FormControl(),
    inhalt: new FormControl(),
  });

  constructor(
    private api: ApiService,
  ) { }

  ngOnInit(): void {
  }

  save() {
    console.log(this.group.value);

    this.api.createPost(this.group.value);
  }
}
