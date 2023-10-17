from django.urls import path
from .views import SignUpAPIView, LogInAPIView


urlpatterns = [
    path('', SignUpAPIView.as_view(), name='user-signup'),
    path('login', LogInAPIView.as_view(), name='user-login'),
]
