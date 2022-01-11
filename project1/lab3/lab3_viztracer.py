from viztracer import VizTracer
from lab3 import Constellation
from mpi4py import MPI
import sys
tracer = VizTracer()
tracer.start()

# Execute program
Constellation.parallel(int(sys.argv[1]))
tracer.stop()
tracer.save('lab3_result' + str(MPI.COMM_WORLD.Get_rank()) + '.json')