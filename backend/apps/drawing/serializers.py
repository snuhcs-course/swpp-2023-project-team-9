from rest_framework import serializers
from .models import Drawing, UserDrawing
from apps.user.models import User
import hashlib


class DrawingParticipantSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('username', 'gender', 'type')


class DrawingSerializer(serializers.ModelSerializer):
    participants = DrawingParticipantSerializer(source='participants.all', many=True, read_only=True)

    class Meta:
        model = Drawing
        fields = (
            'id', 'title', 'description', 'image_url', 'ai_image_url', 'gif_url', 'type', 'participants', 'created_at',
            'updated_at')
