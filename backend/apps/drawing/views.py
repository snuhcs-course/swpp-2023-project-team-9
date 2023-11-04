import base64
import hashlib
import io

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
        file = request.data.get('file')
        drawing_id = request.data.get('host_id')
        title = request.data.get('title')
        decode_file = io.BytesIO()
        decode_file.write(base64.b64decode(file))
        decode_file.seek(0)

        object_name = f"drawings/{drawing_id}/{hashlib.sha256(title.encode()).hexdigest()[:10]}"
        s3_client.upload_fileobj(decode_file, 'little-studio', object_name)
        image_url = f"https://little-studio.s3.amazonaws.com/{object_name}"
        Drawing.objects.filter(id=drawing_id).update(title=title, description=request.data.get('description'),
                                                             image_url=image_url, type="COMPLETED")

        # Modify previous data instead of saving new data
        drawing_data = {
            "id": drawing_id,
            "title": title,
            "description": request.data.get('description'),
            "image_url": image_url,
            "type": "COMPLETED"
        }

        print(drawing_data)

        # serializer = DrawingSerializer(data=drawing_data)
        # if serializer.is_valid():
        #     serializer.save()
        return Response(drawing_data, status=status.HTTP_200_OK)


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
