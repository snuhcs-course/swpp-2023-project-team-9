import bcrypt
from rest_framework import status, views
from rest_framework.response import Response
from .serializers import SignUpSerializer, LogInSerializer
from .models import User


class SignUpAPIView(views.APIView):
    def post(self, request):
        serializer = SignUpSerializer(data=request.data)
        if serializer.is_valid():
            password = serializer.validated_data['password']
            serializer.validated_data['password'] = hash_password(password)
            serializer.save()
            return Response(serializer.data)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)


class LogInAPIView(views.APIView):
    def post(self, request):
        serializer = LogInSerializer(data=request.data)
        if serializer.is_valid():
            username = serializer.validated_data['username']
            password = serializer.validated_data['password']
            users = User.objects.filter(username=username)
            for user in users:
                if bcrypt.checkpw(password.encode('utf-8'), user.password.encode("utf-8")):
                    return Response(serializer.data)
        return Response(status=status.HTTP_400_BAD_REQUEST)


def hash_password(password):
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")
