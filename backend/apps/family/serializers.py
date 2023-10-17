from rest_framework import serializers
from .models import Family
from apps.user.models import User


class FamilyMemberSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        exclude = ("id", "family_id", "password")


class FamilyListSerializer(serializers.ModelSerializer):
    users = FamilyMemberSerializer(many=True, read_only=True, source='family_members')

    class Meta:
        model = Family
        fields = ['users']
