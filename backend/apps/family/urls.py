from django.urls import path
from .views import FamilyAPIView

urlpatterns = [
    path('', FamilyAPIView.as_view())
]