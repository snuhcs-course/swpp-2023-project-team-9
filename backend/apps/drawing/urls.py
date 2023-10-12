from django.urls import path
from .views import DrawingSubmitAPIView, DrawingAPIView

urlpatterns = [
    path('', DrawingAPIView.as_view(), name='drawing-list'),
    path('<int:id>/submit', DrawingSubmitAPIView.as_view(), name='drawing-submit'),
]