import base64
import hashlib
import io

import requests
from django.db import connection, transaction
from django.shortcuts import get_object_or_404
from rest_framework import status, views
from rest_framework.response import Response
from django.conf import settings
from .models import Drawing, DrawingUser
from ..user.models import User
from ..family.models import Family, FamilyUser
from .serializers import DrawingSerializer, DrawingCreateSerializer
import json
import uuid


class DrawingAPIView(views.APIView):

    def get(self, request):
        user_id = request.GET.get('user_id')
        if user_id is None:
            return Response({"detail": "user_id is required"}, status=status.HTTP_400_BAD_REQUEST)

        drawings = Drawing.objects.filter(
            host_id=user_id,
            type=Drawing.TypeChoices.COMPLETED
        ).order_by('-id').prefetch_related('participants')
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
        invitation_code = request.data.get('invitation_code')

        drawing = Drawing.objects.filter(invitation_code=invitation_code).first()
        if not drawing:
            return Response({"detail": "Invalid invitation code"}, status=status.HTTP_400_BAD_REQUEST)
        user = User.objects.get(id=user_id)

        # Save to UserDrawing table (assuming you have a model named UserDrawing)
        user_drawing = DrawingUser(user_id=user, drawing_id=drawing)
        user_drawing.save()

        pusher_client = settings.PUSHER_CLIENT
        pusher_client.trigger(invitation_code, 'participant', {'full_name': user.full_name, 'type': "IN"})

        cursor = connection.cursor()
        draw_id = drawing.id

        with transaction.atomic():
            cursor.execute(
                'SELECT full_name from drawing_drawinguser, user_user where drawing_drawinguser.drawing_id = %s and drawing_drawinguser.user_id = user_user.id',
                [draw_id])
            data = cursor.fetchall()

        participants_list = list(data)
        participants_list = [second for first in participants_list for second in first]
        participants_data = {'participants': participants_list}

        return Response(data=participants_data, status=status.HTTP_200_OK)


class DrawingDetailAPIView(views.APIView):

    def get(self, request, id):
        drawing = get_object_or_404(Drawing, id=id)
        serializer = DrawingSerializer(drawing)
        return Response(serializer.data, status=status.HTTP_200_OK)

    def delete(self, request, id):
        drawing = get_object_or_404(Drawing, id=id)
        drawing.delete()
        return Response({"detail": "Drawing deleted successfully"}, status=status.HTTP_200_OK)


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
        # In Testing
        headers = {
            'Content-type': 'application/json',
            'Accept': 'application/json'
        }
        data = {'file': file}
        res = requests.post('http://147.46.15.75:30001', json=data, headers=headers, timeout=120)
        json_response = res.json()
        dab_url = json_response['dab']
        jumping_url = json_response['jumping']
        wave_hello_url = json_response['wave_hello']


        drawing_id = request.data.get('host_id')
        title = request.data.get('title')
        decode_file = io.BytesIO()
        decode_file.write(base64.b64decode(file))
        decode_file.seek(0)

        object_name = f"drawings/{drawing_id}/{hashlib.sha256(title.encode()).hexdigest()[:10]}"
        s3_client.upload_fileobj(decode_file, 'little-studio', object_name)
        image_url = f"https://little-studio.s3.amazonaws.com/{object_name}"
        Drawing.objects.filter(id=drawing_id).update(title=title, description=request.data.get('description'),
                                                     image_url=image_url, type="COMPLETED",
                                                     gif_dab_url=dab_url, gif_jumping_url=jumping_url,
                                                     gif_wave_hello_url=wave_hello_url)
        invitation_code = Drawing.objects.get(id=drawing_id).invitation_code
        drawing_users = DrawingUser.objects.filter(drawing_id=id)
        for first_user in drawing_users:
            for second_user in drawing_users:
                if first_user.user_id.id != second_user.user_id.id:
                    family_id = Family.objects.filter(user_id=first_user.user_id.id).first()
                    FamilyUser.objects.get_or_create(user_id=first_user.user_id, family_id=family_id)

        # Modify previous data instead of saving new data
        drawing_data = {
            "id": drawing_id,
            "title": title,
            "description": request.data.get('description'),
            "image_url": image_url,
            "type": "COMPLETED"
        }
        pusher_client = settings.PUSHER_CLIENT
        pusher_client.trigger(invitation_code, 'finish', {'image_url': image_url})
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
