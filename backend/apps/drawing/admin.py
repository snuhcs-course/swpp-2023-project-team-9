from django.contrib import admin

from apps.drawing.models import Drawing, DrawingUser, Voice


class DrawingAdmin(admin.ModelAdmin):
    pass


class DrawingUserAdmin(admin.ModelAdmin):
    pass


class VoiceAdmin(admin.ModelAdmin):
    pass


admin.site.register(Drawing, DrawingAdmin)

admin.site.register(DrawingUser, DrawingUserAdmin)

admin.site.register(Voice, VoiceAdmin)
