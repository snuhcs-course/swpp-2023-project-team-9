from django.contrib import admin

from apps.drawing.models import Drawing, UserDrawing, Voice


class DrawingAdmin(admin.ModelAdmin):
    pass


class UserDrawingAdmin(admin.ModelAdmin):
    pass


class VoiceAdmin(admin.ModelAdmin):
    pass


admin.site.register(Drawing, DrawingAdmin)

admin.site.register(UserDrawing, UserDrawingAdmin)

admin.site.register(Voice, VoiceAdmin)
