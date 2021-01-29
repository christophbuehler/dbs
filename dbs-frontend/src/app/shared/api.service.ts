import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, ReplaySubject } from 'rxjs';
import { SubSink } from 'subsink';
import { first, pluck, switchMap, take, tap } from 'rxjs/operators';

const API_BASE = 'http://wwwlab.cs.univie.ac.at/~christophb77/dbs/index.php?rif=';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  unis: Observable<Uni[]>;
  posts: Observable<Post[]>;
  selectedUni: Observable<Uni>;

  private unisSub = new BehaviorSubject<Uni[]>([]);
  private postsSub = new BehaviorSubject<Post[]>([]);
  private selectedUniSub = new ReplaySubject<Uni>(1);

  constructor(
    private http: HttpClient,
  ) {
    this.unis = this.unisSub.asObservable();
    this.posts = this.postsSub.asObservable();
    this.selectedUni = this.selectedUniSub.asObservable();

    this.selectedUni.pipe(
      tap(uni => this.fetchPosts(uni.ID)),
    ).subscribe();
  }

  selectUni(uni: Uni) {
    this.selectedUniSub.next(uni);
  }

  fetchUnis() {
    this.http.get(`${API_BASE}/uni`).pipe(
      pluck('data'),
      tap(unis => this.selectUni(unis[0])),
      tap(unis => this.unisSub.next(unis as any)),
    ).subscribe();
  }

  removePost(postId: number) {
    return this.selectedUniSub.pipe(
      tap(uni => {
        this.http.delete(`${API_BASE}/post/${uni.ID}/${postId}`).subscribe();
      }),
    );
  }

  fetchPosts(uniId: number) {
    this.http.get(`${API_BASE}/post/${uniId}`).pipe(
      pluck('data'),
      tap(posts => this.postsSub.next(posts as any)),
    ).subscribe();
  }

  fetchPostResponses(postId: number) {
    return this.selectedUniSub.pipe(
      take(1),
      switchMap(uni => this.http.get(`${API_BASE}/post/${uni.ID}/${postId}`)),
      pluck('data'),
    );
  }

  createPost(data: any) {
    return this.selectedUniSub.pipe(
      first(),
      tap(uni => {
        this.http.post(`${API_BASE}/post/${uni.ID}`, data).subscribe();
      }),
    );
  }

  createReply(data: any, refPostId: number) {
    return this.selectedUniSub.pipe(
      first(),
      tap(uni => {
        this.http.post(`${API_BASE}/post/${uni.ID}/${refPostId}`, data).subscribe();
      }),
    );
  }
}

export interface Uni {
  ID: number;
  name: string;
}

export interface Post {
  ID: number;
  title: string;
  content: string;
}
