import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ReviewService } from './reviews.service';

@Component({
  selector: 'app-reviews',
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.css']
})
export class ReviewsComponent implements OnInit{
  entityType: string = '';
  userId: string = '';
  entityId: string = '';
  reviewId: string = '';
  reviewComment: string = '';
  reviewName: string = '';
  reviewRating: number = 0.0;
  reviews: any[] = [];
  accessToken: string | null | undefined;
  action: any;

  constructor(
    private reviewService: ReviewService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.userId = params['userId'];
      this.accessToken = params['accessToken'];
      this.entityType = params['entityType'];
      this.entityId = params['entityId'];
      this.reviewId = params['reviewId'];
      console.log('queryParams:', params);
      this.getReviews();
    });
    this.setDefaultAction();
  }

  createReview(): void {
    console.log('Creating review with userId:', this.userId, 'entityType:', this.entityType, 'entityId:', this.entityId);
    if (this.userId && this.entityType && this.entityId && (this.reviewName || this.reviewComment || this.reviewRating)) {
      const createReviews = {
        name: this.reviewName,
        comment: this.reviewComment,
        rating: this.reviewRating
      };

      const url = `/review?userId=${this.userId}&entityType=${this.entityType}&entityId=${this.entityId}`;

      this.reviewService.postReview(url, createReviews).subscribe(
        (response: any) => {
          console.log('Review created successfully:', response);
          this.reviewId = response.reviewId;
          // Reset form values after successful creation
          
          this.reviewName = '';
          this.reviewComment = '';
          this.reviewRating = 0.0;

          // Navigate to the route with the reviewId in the URL
          // Navigate to the route with the reviewId in the URL
          // Navigate to the route with the reviewId as a query parameter
          this.router.navigate(['/reviews'], {
            queryParams: {
              userId: this.userId,
              accessToken: this.accessToken,
              entityType: this.entityType,
              entityId: this.entityId,
              reviewId: this.reviewId
            }
          });
                 
          this.getReviews();
        },
        error => {
          console.error('Failed to create review:', error);
          // Handle error, e.g., show an error message to the user
        }
      );
    } else {
      // Handle invalid input or missing userId, e.g., show a validation message
    }
  }

  performCreateOrUpdateReview(): void {
    console.log('Performing action:', this.action);
    if (this.action === 'update') {
      console.log('Updating review');
      this.updateReview();
    } else if (this.action === 'delete') {
      console.log('Deleting review');
      this.deleteReview();
    } else {
      console.log('Creating review');
      this.createReview();
    }
  }

  updateReview(): void {
    console.log('Updating review with userId:', this.userId, 'entityType:', this.entityType, 'entityId:', this.entityId, 'reviewId:', this.reviewId);
    if (this.userId && this.entityType && this.entityId && this.reviewId && (this.reviewName || this.reviewComment || this.reviewRating)) {
      const updateReviews = {
        name: this.reviewName,
        comment: this.reviewComment,
        rating: this.reviewRating
      };

      const url = `/review?userId=${this.userId}&entityType=${this.entityType}&entityId=${this.entityId}&reviewId=${this.reviewId}`;

      this.reviewService.updateReview(url, updateReviews).subscribe(
        response => {
          console.log('Review updated successfully:', response);

          // Update the reviews array with the updated review
          const updatedReviewIndex = this.reviews.findIndex(r => r.id === this.reviewId);
          if (updatedReviewIndex !== -1) {
            this.reviews[updatedReviewIndex] = response; // Assuming response is the updated review object
          }

          // Call getReviews to refresh the list with the updated review
          this.getReviews();
        },
        error => {
          console.error('Failed to update review:', error);
          // Handle error, e.g., show an error message to the user
        }
      );
    } else {
      // Handle invalid input or missing userId, e.g., show a validation message
    }
  }

  deleteReview(): void {
    console.log('Deleting review with userId:', this.userId, 'entityType:', this.entityType, 'entityId:', this.entityId, 'reviewId:', this.reviewId);
    if (this.userId && this.entityType && this.entityId && this.reviewId) {
      const url = `/review?userId=${this.userId}&entityType=${this.entityType}&entityId=${this.entityId}&reviewId=${this.reviewId}`;

      this.reviewService.deleteReview(url).subscribe(
        response => {
          console.log('Review deleted successfully:', response);

          // Remove the deleted review from the reviews array
          this.reviews = this.reviews.filter(r => r.id !== this.reviewId);

          // Call getReviews to refresh the list without the deleted review
          this.getReviews();
        },
        error => {
          console.error('Failed to delete review:', error);
          // Handle error, e.g., show an error message to the user
        }
      );
    } else {
      // Handle invalid input or missing userId, e.g., show a validation message
    }
  }

  getReviews(): void {
    console.log('Getting reviews with userId:', this.userId, 'entityType:', this.entityType, 'entityId:', this.entityId);
    if (this.userId && this.entityType && this.entityId) {
      const url = `/reviews?userId=${this.userId}&entityType=${this.entityType}&entityId=${this.entityId}`;

      this.reviewService.getReviews(url).subscribe(
        (response: any[]) => {
          this.reviews = response;
          console.log('Reviews retrieved successfully:', this.reviews);
        },
        error => {
          console.error('Failed to get reviews:', error);
        }
      );
    } else {
      // Handle invalid input or missing userId, e.g., show a validation message
    }
  }

  setRating(value: number): void {
    this.reviewRating = value;
  }

  setAction(action: string): void {
    this.action = action;
  }

  setDefaultAction(): void {
    this.action = 'create';
  }
}