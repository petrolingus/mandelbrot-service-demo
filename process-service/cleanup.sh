#!/bin/bash

kubectl delete services process-service -n mandelbrot
kubectl delete deployment process-service -n mandelbrot