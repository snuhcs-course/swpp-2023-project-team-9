from django.db import models


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
        RAW = 'RAW'
        PROCESSED = 'PROCESSED'
        ANIMATED = 'ANIMATED'

    id = models.BigAutoField(primary_key=True)
    title = models.CharField(max_length=255, null=True, blank=True)
    description = models.TextField(null=True, blank=True)

    image_url = models.TextField(null=True, blank=True)
    ai_image_url = models.TextField(null=True, blank=True)
    gif_url = models.TextField(null=True, blank=True)
    type = models.CharField(choices=TypeChoices.choices, max_length=255, default=TypeChoices.RAW)

    voice_id = models.OneToOneField(Voice, on_delete=models.CASCADE, db_column="voice_id", null=True, blank=True)
    host_id = models.ForeignKey(User, on_delete=models.CASCADE, db_column="host_id")
    participants = models.ManyToManyField(User, blank=True, related_name='user_drawing', through='UserDrawing')

    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.title


class UserDrawing(models.Model):
    user_id = models.ForeignKey(User, on_delete=models.CASCADE, db_column="user_id")
    drawing_id = models.ForeignKey(Drawing, on_delete=models.CASCADE, db_column="drawing_id")
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
