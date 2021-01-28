import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EditPostComponent } from '../edit-post/edit-post.component';
import { Post } from '../shared/api.service';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss']
})
export class PostComponent implements OnInit {
  @Input() post: Post;

  constructor(
    private dialog: MatDialog,
  ) { }

  ngOnInit(): void {
  }

  respond() {
    this.openRespondDialog();
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
      // filter(Boolean),
      // withLatestFrom(this.api.selectedUni),
      // tap(([, uni]) => this.api.fetchPosts(uni.ID)),
    ).subscribe();
  }
}
