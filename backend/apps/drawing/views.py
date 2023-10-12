from django.shortcuts import get_object_or_404
from rest_framework import status, views
from rest_framework.response import Response
from django.conf import settings
from .models import Drawing
from .serializers import DrawingSerializer


# Create your views here.
class DrawingAPIView(views.APIView):

    def get(self, request):
        user_id = request.GET.get('user_id')
        if user_id is None:
            return Response({"detail": "user_id is required"}, status=status.HTTP_400_BAD_REQUEST)

        drawings = Drawing.objects.filter(host_id=user_id).prefetch_related('participants')
        serializer = DrawingSerializer(drawings, many=True)
        return Response({"drawings": serializer.data}, status=status.HTTP_200_OK)

class DrawingSubmitAPIView(views.APIView):

    def post(self, request, id):
        s3_client = settings.S3_CLIENT
        file = request.FILES.get('file')
        host_id = request.data.get('host_id')

        object_name = f"drawings/{host_id}/{file.name}"
        s3_client.upload_fileobj(file, 'little-studio', object_name)
        image_url = f"https://little-studio.s3.amazonaws.com/{object_name}"

        # Modify previous data instead of saving new data
        drawing_data = {
            "title": request.data.get('title'),
            "description": request.data.get('description'),
            "image_url": image_url,
            "host_id": host_id,
            "voice_id": request.data.get('voice_id'),
            "type": "PROCESSED"
        }

        serializer = DrawingSerializer(data=drawing_data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
