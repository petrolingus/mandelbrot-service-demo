#!/bin/bash

kubectl create namespace mandelbrot

kubectl apply -f kubernetes/deployment.yaml -n mandelbrot
kubectl apply -f kubernetes/service.yaml -n mandelbrot
kubectl apply -f kubernetes/ingress.yaml -n mandelbrot

#kubectl expose deployment process-service --type=LoadBalancer --name=process-service -n mandelbrot