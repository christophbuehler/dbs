import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SubSink } from 'subsink';
import { pluck, tap } from 'rxjs/operators';

const API_BASE = 'http://wwwlab.cs.univie.ac.at/~christophb77/dbs/index.php?rif=';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  unis: Observable<Uni[]>;
  posts: Observable<Post[]>;

  private unisSub = new BehaviorSubject<Uni[]>([]);
  private postsSub = new BehaviorSubject<Post[]>([]);

  constructor(
    private http: HttpClient,
  ) {
    this.unis = this.unisSub.asObservable();
    this.posts = this.postsSub.asObservable();
  }

  fetchUnis() {
    this.http.get(`${API_BASE}/uni`).pipe(
      pluck('data'),
      tap(unis => console.log('UNIS', unis)),
    ).subscribe();
  }
}

export interface Uni {
  id: number;
  name: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
}
