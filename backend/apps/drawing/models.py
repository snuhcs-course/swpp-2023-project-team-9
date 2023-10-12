from django.db import models
from django.utils import timezone


# Create your models here.
class Drawing(models.Model):
    class TypeChoices(models.TextChoices):
        RAW = 'raw'
        PROCESSED = 'processed'
        ANIMATED = 'animated'

    id = models.BigAutoField(primary_key=True)
    title = models.CharField(max_length=255, null=True, blank=True)
    description = models.TextField(null=True, blank=True)
    image = models.ImageField(upload_to="raw/", blank=True, null=True)
    ai_image = models.ImageField(upload_to="ai/", blank=True, null=True)
    gif_image = models.ImageField(upload_to="gif/", blank=True, null=True)

    type = models.CharField(choices=TypeChoices.choices, max_length=255)

    # voice_id = models.OneToOneField(Voice, on_delete=models.CASCADE, null=True, blank=True)
    # host_id = models.ForeignKey(User, on_delete=models.CASCADE)
    # drawing_users = models.ManyToManyField(User, null=True, blank=True, related_name='user_drawing', through='UserDrawing')

    created_at = models.DateTimeField(default=timezone.now())
    updated_at = models.DateTimeField(blank=True, null=True)

    def __str__(self):
        return self.title