import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { filter, tap } from 'rxjs/operators';
import { EditPostComponent } from '../edit-post/edit-post.component';
import { Post, ApiService } from '../shared/api.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss']
})
export class PostComponent implements OnInit {
  @Input() post: Post;

  showResponses = false;
  responses: any[];
  removed = false;

  constructor(
    private dialog: MatDialog,
    private api: ApiService,
  ) { }

  ngOnInit(): void {
  }

  respond() {
    this.openRespondDialog();
  }

  loadResponses() {
    this.showResponses = true;
    this.api.fetchPostResponses(this.post.ID).pipe(
      tap(resp => console.log('resp', resp)),
      tap((resp : any) => this.responses = resp),
    ).subscribe();
  }

  removePost() {
    this.api.removePost(this.post.ID).pipe(
      tap(() => this.removed = true),
    ).subscribe();
  }

  openRespondDialog() {
    let dialogRef = this.dialog.open(EditPostComponent, {
      height: '400px',
      width: '600px',
      data: {
        refPostId: this.post.ID,
      }
    });

    // reload posts if added successfully
    dialogRef.afterClosed().pipe(
      filter(Boolean),
      tap(() => this.loadResponses()),
    ).subscribe();
  }
}
