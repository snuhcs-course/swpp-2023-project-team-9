from rest_framework import serializers

from .models import User


class SignUpSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = '__all__'

    def to_representation(self, instance):
        return {
            'id': instance.id,
            'full_name': instance.full_name,
            'username': instance.username,
            'gender': instance.gender,
            'type': instance.type,
            'created_at': instance.created_at
        }


class LogInSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ["username", "password"]

    def to_representation(self, instance):
        return {
            'id': instance.id,
            'full_name': instance.full_name,
            'username': instance.username,
            'gender': instance.gender,
            'type': instance.type,
            'created_at': instance.created_at
        }
