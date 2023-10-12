from django.db import models

class Family(models.Model):
    id = models.BigAutoField(primary_key=True)
    host_id = models.ForeignKey('user.User', on_delete=models.CASCADE, db_column="host_id", related_name='host_family')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
