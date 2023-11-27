from django.db import models
from apps.user.models import User

class Voice(models.Model):
    id = models.BigAutoField(primary_key=True)
    title = models.CharField(max_length=255)
    file = models.FileField(upload_to="voice/")
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.title


class Drawing(models.Model):
    class TypeChoices(models.TextChoices):
        NOT_STARTED = 'NOT STARTED' 
        STARTED = 'STARTED' 
        COMPLETED = 'COMPLETED'
        AI_PROCESSED = 'AI_PROCESSED'
        GIF_PROCESSED = 'GIF_PROCESSED'

    id = models.BigAutoField(primary_key=True)
    title = models.CharField(max_length=255, null=True, blank=True)
    description = models.TextField(null=True, blank=True)
    invitation_code = models.CharField(max_length=255, null=True, blank=True)

    image_url = models.TextField(null=True, blank=True)
    gif_dab_url = models.TextField(null=True, blank=True)
    gif_jumping_url = models.TextField(null=True, blank=True)
    gif_wave_hello_url = models.TextField(null=True, blank=True)
    type = models.CharField(choices=TypeChoices.choices, max_length=255, default=TypeChoices.NOT_STARTED)

    host_id = models.ForeignKey(User, on_delete=models.CASCADE, db_column="host_id")
    participants = models.ManyToManyField(User, blank=True, related_name='drawing_user', through='DrawingUser')

    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.id)


class DrawingUser(models.Model):
    user_id = models.ForeignKey(User, on_delete=models.CASCADE, db_column="user_id")
    drawing_id = models.ForeignKey(Drawing, on_delete=models.CASCADE, db_column="drawing_id")
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)