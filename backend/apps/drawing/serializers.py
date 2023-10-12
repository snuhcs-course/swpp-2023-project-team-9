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


class DrawingCreateSerializer(serializers.ModelSerializer):
    invitation_code = serializers.SerializerMethodField()

    class Meta:
        model = Drawing
        fields = ('id', 'host_id', 'invitation_code', 'created_at')

    def create(self, validated_data):
        drawing = Drawing.objects.create(**validated_data)
        UserDrawing.objects.create(user_id=validated_data['host_id'], drawing_id=drawing)
        return drawing

    def get_invitation_code(self, obj):
        return hashlib.sha256(str(obj.id).encode()).hexdigest()[:10]  # Taking first 10 characters as an example