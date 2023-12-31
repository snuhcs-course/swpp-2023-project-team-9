from rest_framework.views import APIView
from rest_framework.response import Response
from .models import Family
from .serializers import FamilyListSerializer


class FamilyAPIView(APIView):
    def get(self, request):
        user_id = request.GET.get('user_id')
        if not user_id:
            return Response({'error': 'user_id is required'}, status=400)

        try:
            family = Family.objects.get(user_id=user_id)
        except Family.DoesNotExist:
            return Response({'error': 'Family not found'}, status=404)

        serializer = FamilyListSerializer(family, context={'user_id_to_exclude': user_id})
        return Response(serializer.data)
