mpiexec -np 8 python3 lab2_viztracer.py
viztracer --combine ./lab2*.json
vizviewer result.json