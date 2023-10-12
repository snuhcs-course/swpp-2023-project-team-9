from django.urls import path
from .views import DrawingSubmitAPIView

urlpatterns = [
    path('<int:id>/submit', DrawingSubmitAPIView.as_view(), name='drawing-submit'),
]