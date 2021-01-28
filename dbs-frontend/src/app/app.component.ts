import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ApiService } from './shared/api.service';
import { EditPostComponent } from './edit-post/edit-post.component';
import { filter, tap, withLatestFrom } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {

  constructor(
    public api: ApiService,
    private dialog: MatDialog,
  ) {
    api.fetchUnis();
  }

  openNewDialog() {
    let dialogRef = this.dialog.open(EditPostComponent, {
      height: '400px',
      width: '600px',
    });

    // reload posts if added successfully
    dialogRef.afterClosed().pipe(
      filter(Boolean),
      withLatestFrom(this.api.selectedUni),
      tap(([, uni]) => this.api.fetchPosts(uni.ID)),
    ).subscribe();
  }
}
