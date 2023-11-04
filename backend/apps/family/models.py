from django.db import models

class Family(models.Model):
    id = models.BigAutoField(primary_key=True)
    user_id = models.ForeignKey('user.User', on_delete=models.CASCADE, db_column="user_id", related_name='user_family')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

class FamilyUser(models.Model):
    id = models.BigAutoField(primary_key=True)
    family_id = models.ForeignKey(Family, on_delete=models.CASCADE, db_column="family_id", related_name='family_users')
    user_id = models.ForeignKey('user.User', on_delete=models.CASCADE, db_column="user_id", related_name='user_family_users')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)