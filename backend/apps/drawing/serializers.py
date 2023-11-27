from rest_framework import serializers
from .models import Drawing, DrawingUser
from apps.user.models import User
import hashlib


class DrawingParticipantSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'full_name', 'username', 'gender', 'type')


class DrawingSerializer(serializers.ModelSerializer):
    participants = DrawingParticipantSerializer(source='participants.all', many=True, read_only=True)

    class Meta:
        model = Drawing
        fields = (
            'id', 'title', 'description', 'image_url', 'gif_dab_url', 'gif_jumping_url', 'gif_wave_hello_url', 'type', 'participants', 'created_at',
            'updated_at')


class DrawingCreateSerializer(serializers.ModelSerializer):
    invitation_code = serializers.SerializerMethodField()

    class Meta:
        model = Drawing
        fields = ('id', 'host_id', 'invitation_code', 'created_at')

    def create(self, validated_data):
        drawing = Drawing.objects.create(**validated_data)
        drawing.invitation_code = self.get_invitation_code(drawing)
        drawing.save()
        DrawingUser.objects.create(user_id=validated_data['host_id'], drawing_id=drawing)
        return drawing

    def get_invitation_code(self, obj):
        return hashlib.sha256(str(obj.id).encode()).hexdigest()[:6]