from django.urls import path
from .views import DrawingSubmitAPIView, DrawingAPIView, DrawingDetailAPIView

urlpatterns = [
    path('', DrawingAPIView.as_view(), name='drawing-list'),
    path('<int:id>', DrawingDetailAPIView.as_view(), name='drawing-detail'),
    path('<int:id>/submit', DrawingSubmitAPIView.as_view(), name='drawing-submit'),
]