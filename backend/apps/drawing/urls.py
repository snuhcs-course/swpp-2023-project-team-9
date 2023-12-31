from django.urls import path
from .views import DrawingSubmitAPIView, DrawingAPIView, DrawingDetailAPIView, DrawingJoinAPIView, DrawingStartAPIView, DrawingWaitAPIView, DrawingAbortAPIView, DrawingRealTimeAPIView

urlpatterns = [
    path('', DrawingAPIView.as_view(), name='drawing-list'),
    path('join', DrawingJoinAPIView.as_view(), name='drawing-join'),
    path('<int:id>', DrawingDetailAPIView.as_view(), name='drawing-detail'),
    path('<int:id>/submit', DrawingSubmitAPIView.as_view(), name='drawing-submit'),
    path('<int:id>/canvas', DrawingRealTimeAPIView.as_view(), name='drawing-real-time'),
    path('<int:id>/start', DrawingStartAPIView.as_view(), name='drawing-start'),
    path('<int:id>/wait', DrawingWaitAPIView.as_view(), name='drawing-wait'),
    path('<int:id>/abort', DrawingAbortAPIView.as_view(), name='drawing-abort'),
]