from django.db import models


class User(models.Model):
    class GenderChoices(models.TextChoices):
        MALE = 'MALE'
        FEMALE = 'FEMALE'
        OTHER = 'OTHER'

    class TypeChoices(models.TextChoices):
        PARENT = 'PARENT'
        CHILD = 'CHILD'

    id = models.BigAutoField(primary_key=True)
    username = models.CharField(max_length=255)
    password = models.CharField(max_length=255)
    gender = models.CharField(choices=GenderChoices.choices, max_length=255)
    type = models.CharField(choices=TypeChoices.choices, max_length=255)
    family_id = models.ForeignKey('family.Family', on_delete=models.CASCADE, db_column="family_id",
                                  related_name='family_members', null=True, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.username
