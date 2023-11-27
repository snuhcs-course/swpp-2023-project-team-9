from django.contrib import admin

from apps.drawing.models import Drawing, DrawingUser


class DrawingAdmin(admin.ModelAdmin):
    pass


class DrawingUserAdmin(admin.ModelAdmin):
    pass


admin.site.register(Drawing, DrawingAdmin)

admin.site.register(DrawingUser, DrawingUserAdmin)
