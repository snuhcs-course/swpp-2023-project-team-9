from django.shortcuts import get_object_or_404
from rest_framework import status, views
from rest_framework.response import Response
from django.conf import settings
from .models import Drawing, UserDrawing
from .serializers import DrawingSerializer, DrawingCreateSerializer
import json
import uuid

from ..user.models import User


class DrawingAPIView(views.APIView):

    def get(self, request):
        user_id = request.GET.get('user_id')
        if user_id is None:
            return Response({"detail": "user_id is required"}, status=status.HTTP_400_BAD_REQUEST)

        drawings = Drawing.objects.filter(host_id=user_id).prefetch_related('participants')
        serializer = DrawingSerializer(drawings, many=True)
        return Response({"drawings": serializer.data}, status=status.HTTP_200_OK)

    def post(self, request):
        serializer = DrawingCreateSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class DrawingJoinAPIView(views.APIView):

    def post(self, request):
        user_id = int(request.data.get('user_id'))
        # TODO : After implement login feature, check invitation Code validation.
        invitation_code = request.data.get('invitation_code')

        # Validate the invitation code (you may have a different method to check the code)
        drawing = Drawing.objects.filter(invitation_code=invitation_code).first()
        if not drawing:
            return Response({"detail": "Invalid invitation code"}, status=status.HTTP_400_BAD_REQUEST)
        user = User.objects.get(id=user_id)
        username = user.username
        pusher_client = settings.PUSHER_CLIENT

        pusher_client.trigger(invitation_code, 'participant', {'username': username, 'type': "IN"})
        # Save to UserDrawing table (assuming you have a model named UserDrawing)
        user_drawing = UserDrawing(user_id=user, drawing_id=drawing)
        user_drawing.save()

        return Response({"detail": "Successfully joined the drawing"}, status=status.HTTP_200_OK)


class DrawingDetailAPIView(views.APIView):

    def get(self, request, id):
        drawing = get_object_or_404(Drawing, id=id)
        serializer = DrawingSerializer(drawing)
        return Response(serializer.data, status=status.HTTP_200_OK)


class DrawingStartAPIView(views.APIView):

    def post(self, request, id):
        invitation_code = request.data.get('invitationCode')

        pusher_client = settings.PUSHER_CLIENT
        pusher_client.trigger(invitation_code, 'participant', {'start': True})

        return Response({"detail": "Drawing started successfully"}, status=status.HTTP_200_OK)


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
            "type": "COMPLETED"
        }

        serializer = DrawingSerializer(data=drawing_data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class DrawingRealTimeAPIView(views.APIView):

    def post(self, request, id):
        pusher_client = settings.PUSHER_CLIENT
        stroke_data = request.data.get('stroke_data')
        invitation_code = request.data.get('invitationCode')
        serialized_data = json.dumps({'stroke_data': stroke_data})
        
        chunk_size = 5000  # Adjust this size
        msg_id = str(id)  # Drawing ID
        stroke_id = str(uuid.uuid4())  # Unique ID for each stroke
        
        if len(serialized_data) <= chunk_size:
            pusher_client.trigger(invitation_code, 'new-stroke', {'stroke_data': stroke_data})
        else:
            for i in range(0, len(serialized_data), chunk_size):
                chunk = serialized_data[i:i + chunk_size]
                is_final = i + chunk_size >= len(serialized_data)
                pusher_client.trigger(
                    invitation_code,
                    'chunked-new-stroke', 
                    {
                        'id': msg_id,
                        'stroke_id': stroke_id,
                        'index': int(i / chunk_size),
                        'chunk': chunk,
                        'final': is_final
                    }
                )
        return Response(status=status.HTTP_200_OK)
