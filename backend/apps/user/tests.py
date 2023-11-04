from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase, APIClient
from apps.user.models import User
import bcrypt

class UserTests(APITestCase):
    def setUp(self):
        self.client = APIClient()
        self.signup_url = reverse('user-signup')
        self.login_url = reverse('user-login')
        self.user_data = {
            'username': 'testuser',
            'password': 'testpassword',
            'gender': 'MALE',
            'type': 'PARENT'
        }
        self.user = User.objects.create(
            username=self.user_data['username'],
            password=bcrypt.hashpw(self.user_data['password'].encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        )

    def test_signup(self):
        response = self.client.post(self.signup_url, self.user_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(User.objects.count(), 2)
        self.assertEqual(User.objects.latest('id').username, self.user_data['username'])

    def test_signup_invalid_data(self):
        response = self.client.post(self.signup_url, {}, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_login(self):
        response = self.client.post(self.login_url, {
            'username': self.user_data['username'],
            'password': self.user_data['password']
        }, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['userid'], self.user.id)

    def test_login_wrong_password(self):
        response = self.client.post(self.login_url, {
            'username': self.user_data['username'],
            'password': 'wrongpassword'
        }, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

    def test_login_nonexistent_user(self):
        response = self.client.post(self.login_url, {
            'username': 'nonexistent',
            'password': 'testpassword'
        }, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)