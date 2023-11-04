from django.test import TestCase
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase, APIClient
from apps.drawing.models import Drawing, Voice
from apps.user.models import User


class DrawingTests(APITestCase):
    def setUp(self):
        self.client = APIClient()
        self.user = User.objects.create(username='testuser', password='testpassword')
        self.drawing = Drawing.objects.create(
            title='Test Drawing',
            description='Test Description',
            host_id=self.user,
        )
        self.url = reverse('drawing-list') 
    
    def test_get_drawings(self):
        self.client.force_authenticate(user=self.user)

        response = self.client.get(self.url, {'user_id': self.user.id})

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data['drawings']), 1)
        self.assertEqual(response.data['drawings'][0]['title'], 'Test Drawing')

    def test_post_drawing(self):
        self.client.force_authenticate(user=self.user)
        payload = {
            'host_id': self.user.id,
            # Add other fields if necessary
        }

        response = self.client.post(self.url, payload, format='json')

        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Drawing.objects.count(), 2)
        self.assertEqual(Drawing.objects.last().host_id, self.user)

    def tearDown(self):
        Drawing.objects.all().delete()
        Voice.objects.all().delete()
        User.objects.all().delete()
