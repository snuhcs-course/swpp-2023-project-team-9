from rest_framework import serializers
from .models import Family, FamilyUser 


class FamilyMemberSerializer(serializers.ModelSerializer):
    id = serializers.IntegerField(source='user_id.id')
    full_name = serializers.CharField(source='user_id.full_name')
    username = serializers.CharField(source='user_id.username')
    gender = serializers.CharField(source='user_id.gender')
    type = serializers.CharField(source='user_id.type')

    class Meta:
        model = FamilyUser
        fields = ['id', 'full_name', 'username', 'gender', 'type']


class FamilyListSerializer(serializers.ModelSerializer):
    users = FamilyMemberSerializer(many=True, read_only=True, source='family_users')

    class Meta:
        model = Family
        fields = ['users']
