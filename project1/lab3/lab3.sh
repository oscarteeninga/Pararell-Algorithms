mpiexec -np 8 python3 lab3_viztracer.py 500
viztracer --combine ./lab3*.json
vizviewer result.json