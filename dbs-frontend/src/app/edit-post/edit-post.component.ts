import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ApiService } from '../shared/api.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-post',
  templateUrl: './edit-post.component.html',
  styleUrls: ['./edit-post.component.scss'],
})
export class EditPostComponent implements OnInit {
  group = new FormGroup({
    // autor: new FormControl(),
    titel: new FormControl(),
    inhalt: new FormControl(),
  });
  refPostId: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    public dialogRef: MatDialogRef<EditPostComponent>,
    private api: ApiService
  ) {
    this.refPostId = (data || {}).refPostId;
  }

  ngOnInit(): void {}

  save() {
    if (this.refPostId) {
      return this.api
        .createReply(this.group.value, this.refPostId)
        .subscribe(() => this.dialogRef.close(true));
    }
    this.api
      .createPost(this.group.value)
      .subscribe(() => this.dialogRef.close(true));
  }
}
