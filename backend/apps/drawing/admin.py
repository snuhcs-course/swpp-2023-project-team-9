from django.contrib import admin

from apps.drawing.models import Drawing


# Register your models here.

class DrawingAdmin(admin.ModelAdmin):
    pass


admin.site.register(Drawing, DrawingAdmin)
