# LittleStudio 

"LittleStudio" is an innovative drawing application designed for children aged 5-8 and their parents. The interactive platform allows families to collectively produce unique piece of artwork in their mobile systems.

## Documents 

Please refer to following documents for more detailed explanation about system architecture and application features. 

- [Design Documentation](../../wiki/Design-Documentation)
- [Requirements and Specifications](../../wiki/Requirements-and-Specifications)

## Features

- Real-time drawing across different devices
- Animated artwork
- Gallery shared with family

## Backend Server Setup

#### Clone Repository 

We recommend activating virtual environment for LittleStudio server set up.

```
git clone https://github.com/snuhcs-course/swpp-2023-project-team-9.git
cd backend 
pip install -e .
```

#### Configure Environment Variables 

Please contact the team if you need access to existing AWS RDS and S3 bucket services. Otherwise, create your own resources and place them in an .env file inside backend directory.

#### Run Backend Server

```
python manage.py runserver
```

## Inference Server Setup 

#### Get Authentication for GPU server

Please follow this documentation for GPU service authentication: https://gpu.snucse.org/kubectl.html.

#### Run GPU server

Apply deployment and service yaml in the inference directory. 

```
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```

Find the name of the pod running. 

```
kubectl get pods
```

Connect to the api-server container defined in your deployment. 

```
kubectl exec -it {pod-name} -c api-server /bin/bash
```

Within the pod, activate virtual environment in anaconda and run flask server.

```
activate animated_drawings
cd animated_drawing/examples
flask run --host=0.0.0.0
```

Now you can access inference server from outside. 

When you're done, delete all service and deployement to free up GPU resources on Bacchus server.

```
kubectl delete services --all
kubectl delete deployments --all
```

#### Changing Docker Image in Harbor Registry 
Images are already available in the Bacchus harbor registry.

If you want to change the image or underlying code, please contact us.

Flask server code is located in examples/app.py.



