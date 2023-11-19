Inference Server
===
### Introduction
---
Our backend servers are deployed on amazon ec2, our database is on rds with mysql, and our image files are on s3 bucket.
However, Amazon EC2 was not performant enough for our AI models, and we decided it was better to split up the overly heavy workloads rather than have them all on one server.
We decided it was better to split up the overly heavy workload rather than have it all on one server, so we built a backend based on FACEBOOK's
Animated Drawings on top of Facebook's backend server to enable API communication and put it on the university's GPU
The university's  GPU service uses Kubernetes to allocate and manage resources.

Therefore, we had to create a docker image wrapping Animated Drawings as a backend and put it on kubernetes.
For Animated Drawings, we have a repository that preprocesses the data and a dockerfile that contains the model
to preprocess the data. In the internal code, the two projects are communicating on localhost, so I had to create a single pod with the
I put the backend-wrapped data preprocessing docker image and the model dockerfile together so that they can communicate with each other via port forwarding to
This means that external requests are received from the backend and data preprocessing container
from the backend and data preprocessing containers, pass them to the model container in the same pod, and return the results.

### How to set Up
---

#### Get Authentication for GPU server

Please follow this documentation https://gpu.snucse.org/kubectl.html.

#### Run GPU server

Download the deployment.yaml and service.yaml in the inference folder. 

```
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
```
You need to look up the currently running pod because you need to know the name of the pod to access it.

```
kubectl get pods
```

Connect to the api-server container defined in your deployment. 

```
kubectl exec -it {pod-name} -c api-server /bin/bash
```

activate virtual environment in anaconda and run flask server.

```
activate animated_drawings
cd animated_drawing/examples
flask run —host=0.0.0.0
```
Soon, we'll automate server launches as well.

Finally you can access inference server!

When you're done, delete all services, deployments, and pods to free up GPU resources on Bacchus' server.

```
kubectl delete services --all
kubectl delete deployments --all
kubectl delete pods --all
```


### How to change Docker Image in Bacchus' registry 
---
The dockerfile is already up on the harbor provided by Bacchus' server.

If you want to change the server code, Please contact us.

flask server is in app.py.

